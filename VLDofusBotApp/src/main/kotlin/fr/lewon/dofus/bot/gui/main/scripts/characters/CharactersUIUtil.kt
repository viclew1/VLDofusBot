package fr.lewon.dofus.bot.gui.main.scripts.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.CharacterEditionUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.characters.edit.spells.CharacterSpellsUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.logs.LoggerUIType
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.logs.LogsUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.scriptinfo.ScriptInfoUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.selector.ScriptSelectorUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.util.external.skinator.SkinatorRequestProcessor
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSpellManager
import fr.lewon.dofus.bot.util.filemanagers.impl.ExplorationRecordManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.NetworkAutoUpdater
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock

object CharactersUIUtil : ComposeUIUtil(), CharacterManagerListener, ScriptRunnerListener, GameSnifferListener,
    VldbLoggerListener {

    private val lock = ReentrantLock()
    private val uiStateByCharacterName = HashMap<String, MutableState<CharacterUIState>>()
    private val charactersUIState = mutableStateOf(CharactersUIState(getOrderedCharactersNames()))
    private val characterNameByLogger = HashMap<VldbLogger, String>()

    override fun init() {
        CharacterManager.addListener(this)
        characterNameByLogger.clear()
        for (character in getOrderedCharacters()) {
            removeListeners(character.name)
            addListeners(character)
            getCharacterUIState(character.name)
        }
        GameSnifferUtil.updateNetwork()
        NetworkAutoUpdater.startIfNeeded()
    }

    private fun getOrderedCharacters(): List<DofusCharacter> {
        return CharacterManager.getCharacters().sortedWith(
            compareBy({ getCharacterUIState(it.name).value.activityState.displayOrder }, { it.name })
        )
    }

    private fun getOrderedCharactersNames(): List<String> {
        return getOrderedCharacters().map { it.name }
    }

    private fun addListeners(character: DofusCharacter) {
        ScriptRunner.addListener(character, this)
        GameSnifferUtil.addListener(character, this)
        CharacterSpellManager.addListener(character, CharacterSpellsUIUtil)
        characterNameByLogger[character.executionLogger] = character.name
        characterNameByLogger[character.snifferLogger] = character.name
        character.executionLogger.listeners.add(this)
        character.snifferLogger.listeners.add(this)
    }

    private fun removeListeners(characterName: String) {
        ScriptRunner.removeListener(characterName, this)
        GameSnifferUtil.removeListener(characterName, this)
        CharacterSpellManager.removeListener(characterName, CharacterSpellsUIUtil)
    }

    fun getCharacterUIState(characterName: String): MutableState<CharacterUIState> {
        return lock.executeSyncOperation {
            uiStateByCharacterName.computeIfAbsent(characterName) {
                val character = CharacterManager.getCharacter(characterName)
                    ?: error("Character not found : $characterName")
                mutableStateOf(
                    CharacterUIState(
                        characterName,
                        character.dofusClassId,
                        character.isOtomaiTransportAvailable
                    )
                )
            }
        }
    }

    fun selectCharacter(characterName: String?) {
        lock.executeSyncOperation {
            if (characterName != null) {
                CharacterEditionUIUtil.editCharacter(characterName)
                CharacterSpellsUIUtil.updateSpells(CharacterSpellManager.getSpells(characterName))
            }
            charactersUIState.value = charactersUIState.value.copy(selectedCharacterName = characterName)
            val destTab = if (characterName == null) ScriptTab.GLOBAL else ScriptTab.INDIVIDUAL
            if (ScriptTabsUIUtil.getCurrentTab() != destTab) {
                ScriptTabsUIUtil.updateCurrentTab(destTab)
            }
        }
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        lock.executeSyncOperation {
            addListeners(character)
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
            getCharacterUIState(character.name)
        }
    }

    override fun onCharacterUpdate(character: DofusCharacter) {
        lock.executeSyncOperation {
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
            val uiState = getCharacterUIState(character.name)
            uiState.value = uiState.value.copy(
                dofusClassId = character.dofusClassId,
                isOtomaiTransportAvailable = character.isOtomaiTransportAvailable
            )
        }
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        lock.executeSyncOperation {
            uiStateByCharacterName.remove(character.name)
            val charactersUIState = charactersUIState.value
            val newSelectedCharacter = if (charactersUIState.selectedCharacterName != character.name) {
                charactersUIState.selectedCharacterName
            } else null
            if (newSelectedCharacter == null) {
                ScriptTabsUIUtil.updateCurrentTab(ScriptTab.GLOBAL)
            }
            this.charactersUIState.value = charactersUIState.copy(
                characterNames = getOrderedCharactersNames(),
                selectedCharacterName = newSelectedCharacter,
            )
            ScriptInfoUIUtil.removeScriptInfoUIState(character.name)
            removeListeners(character.name)
        }
    }

    private fun getCharacterLoggersWithTypes(character: DofusCharacter): Map<VldbLogger, LoggerUIType> {
        return mapOf(
            character.snifferLogger to LoggerUIType.SNIFFER,
            character.executionLogger to LoggerUIType.EXECUTION
        )
    }

    override fun onScriptStart(character: DofusCharacter, script: ScriptRunner.RunningScript) {
        lock.executeSyncOperation {
            val characterUIState = getCharacterUIState(character.name)
            characterUIState.value = characterUIState.value.copy(
                activityState = CharacterActivityState.BUSY,
                runningScript = script
            )
            ScriptInfoUIUtil.updateState(character.name)
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = true)
            ExplorationUIUtil.removeAvailableCharacter(character.name)
        }
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        lock.executeSyncOperation {
            val characterState = getCharacterUIState(character.name)
            characterState.value = characterState.value.copy(runningScript = null)
            computeState(character)
            ScriptInfoUIUtil.updateState(character.name)
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = true)
            if (GameSnifferUtil.getFirstConnection(character) != null) {
                ExplorationUIUtil.addAvailableCharacter(character.name)
            }
        }
    }

    override fun onListenStart(character: DofusCharacter) {
        lock.executeSyncOperation {
            val characterState = getCharacterUIState(character.name)
            characterState.value = characterState.value.copy(
                activityState = CharacterActivityState.TO_INITIALIZE
            )
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
            ScriptInfoUIUtil.getScriptInfoUIState(character.name)
            ExplorationUIUtil.addAvailableCharacter(character.name)
        }
    }

    override fun onListenStop(character: DofusCharacter) {
        lock.executeSyncOperation {
            val characterState = getCharacterUIState(character.name)
            characterState.value = CharacterUIState(
                name = character.name,
                dofusClassId = character.dofusClassId,
                isOtomaiTransportAvailable = character.isOtomaiTransportAvailable,
                checked = false,
                activityState = CharacterActivityState.DISCONNECTED,
                flashVars = null,
                skinImage = null,
                currentMap = null
            )
            charactersUIState.value = charactersUIState.value.copy(
                characterNames = getOrderedCharactersNames()
            )
            ScriptRunner.stopScript(character.name)
            ExplorationUIUtil.removeAvailableCharacter(character.name)
        }
    }

    override fun onLogUpdated(logger: VldbLogger, logItem: LogItem) {
        lock.executeSyncOperation {
            val characterName = characterNameByLogger[logger]
                ?: error("Unregistered logger character")
            val character = CharacterManager.getCharacter(characterName)
                ?: error("Character not found : $characterName")
            val loggerUIType = getCharacterLoggersWithTypes(character)[logger]
                ?: error("Unregistered logger type")
            LogsUIUtil.updateLogItem(characterName, loggerUIType, logItem)
        }
    }

    fun updateState(character: DofusCharacter) {
        lock.executeSyncOperation {
            val characterUIState = getCharacterUIState(character.name)
            if (characterUIState.value.activityState != CharacterActivityState.BUSY) {
                computeState(character)
            }
        }
    }

    private fun computeState(character: DofusCharacter) {
        val connection = GameSnifferUtil.getFirstConnection(character)
        val newActivityState = getNewActivityState(connection)
        lock.executeSyncOperation {
            val characterState = getCharacterUIState(character.name)
            characterState.value = characterState.value.copy(
                activityState = newActivityState,
            )
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
        }
    }

    private fun getNewActivityState(connection: DofusConnection?): CharacterActivityState {
        return if (connection != null) {
            if (GameSnifferUtil.getGameInfoByConnection(connection).shouldInitBoard) {
                CharacterActivityState.TO_INITIALIZE
            } else {
                CharacterActivityState.AVAILABLE
            }
        } else {
            CharacterActivityState.DISCONNECTED
        }
    }

    fun getAllCharacterUIStates(): List<MutableState<CharacterUIState>> {
        return charactersUIState.value.characterNames.map { getCharacterUIState(it) }
    }

    fun getSelectedCharactersUIStates(): List<MutableState<CharacterUIState>> {
        return when (ScriptTabsUIUtil.getCurrentTab()) {
            ScriptTab.GLOBAL -> getCheckedCharactersUIStates()
            ScriptTab.INDIVIDUAL -> listOf(getSelectedCharacterUIState() ?: error("A character should be selected"))
        }
    }

    private fun getCheckedCharactersUIStates(): List<MutableState<CharacterUIState>> {
        return lock.executeSyncOperation {
            uiStateByCharacterName.values.filter { it.value.checked }
        }
    }

    fun getSelectedCharacterUIState(): MutableState<CharacterUIState>? {
        return charactersUIState.value.selectedCharacterName?.let { getCharacterUIState(it) }
    }

    fun updateSkin(character: DofusCharacter, entityLook: EntityLook) {
        lock.executeSyncOperation {
            val characterUIState = getCharacterUIState(character.name)
            val newFlashVars = SkinatorRequestProcessor.getFlashVars(entityLook)
            if (newFlashVars != characterUIState.value.flashVars) {
                refreshSkin(characterUIState.value.name, newFlashVars)
            }
        }
    }

    fun updateMap(character: DofusCharacter, map: DofusMap) {
        lock.executeSyncOperation {
            val uiState = getCharacterUIState(character.name)
            uiState.value = uiState.value.copy(currentMap = map)
            Thread {
                ExplorationRecordManager.exploreMap(map.id)
                ExplorationUIUtil.requestMapUpdate()
            }.start()
        }
    }

    fun refreshSkin(characterName: String, newFlashVars: String? = null) {
        lock.executeSyncOperation {
            val characterUIState = getCharacterUIState(characterName)
            val flashVars = newFlashVars ?: characterUIState.value.flashVars
            characterUIState.value = characterUIState.value.copy(
                flashVars = flashVars,
                skinImageState = SkinImageState.LOADING
            )
            if (flashVars != null) {
                Thread {
                    doRefreshSkin(characterUIState.value.name, flashVars)
                }.start()
            }
        }
    }

    private fun doRefreshSkin(characterName: String, newFlashVars: String) {
        val newSkinImagePainter = try {
            SkinatorRequestProcessor.getSkinImage(newFlashVars).toPainter()
        } catch (e: IOException) {
            println("Failed to retrieve skin image : ${e.message}")
            null
        }
        lock.executeSyncOperation {
            val characterUIState = getCharacterUIState(characterName)
            characterUIState.value = characterUIState.value.copy(
                skinImage = newSkinImagePainter,
                skinImageState = if (newSkinImagePainter == null) SkinImageState.BROKEN else SkinImageState.LOADED
            )
        }
    }

}