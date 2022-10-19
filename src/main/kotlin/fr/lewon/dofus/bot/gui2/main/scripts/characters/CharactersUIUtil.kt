package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import fr.lewon.dofus.bot.core.utils.LockUtils
import fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.CharacterEditionUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.spells.CharacterSpellsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LoggerUIType
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LogsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo.ScriptInfoUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.selector.ScriptSelectorUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.util.external.skinator.SkinatorRequestProcessor
import fr.lewon.dofus.bot.util.external.skinator.SkinatorUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSpellManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.NetworkAutoUpdater
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.util.concurrent.locks.ReentrantLock

object CharactersUIUtil : CharacterManagerListener, ScriptRunnerListener, GameSnifferListener, VldbLoggerListener {

    private val lock = ReentrantLock()
    private val uiStateByCharacterName = HashMap<String, MutableState<CharacterUIState>>()
    private val charactersUIState = mutableStateOf(CharactersUIState(getOrderedCharactersNames()))
    private val characterNameByLogger = HashMap<VldbLogger, String>()

    fun initListeners() {
        CharacterManager.addListener(this)
        for (character in getOrderedCharacters()) {
            addListeners(character)
        }
        NetworkAutoUpdater.start()
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
        return LockUtils.executeSyncOperation(lock) {
            uiStateByCharacterName.computeIfAbsent(characterName) {
                val character = CharacterManager.getCharacter(characterName)
                    ?: error("Character not found : $characterName")
                mutableStateOf(CharacterUIState(characterName, character.dofusClassId))
            }
        }
    }

    fun selectCharacter(characterName: String?) {
        LockUtils.executeSyncOperation(lock) {
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
        LockUtils.executeSyncOperation(lock) {
            addListeners(character)
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
            getCharacterUIState(character.name)
        }
    }

    override fun onCharacterUpdate(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
            val uiState = getCharacterUIState(character.name)
            uiState.value = uiState.value.copy(dofusClassId = character.dofusClassId)
        }
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
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
        LockUtils.executeSyncOperation(lock) {
            val characterUIState = getCharacterUIState(character.name)
            characterUIState.value = characterUIState.value.copy(
                activityState = CharacterActivityState.BUSY,
                runningScript = script
            )
            ScriptInfoUIUtil.updateState(character.name)
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = true)
        }
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        LockUtils.executeSyncOperation(lock) {
            val characterState = getCharacterUIState(character.name)
            characterState.value = characterState.value.copy(runningScript = null)
            computeState(character)
            ScriptInfoUIUtil.updateState(character.name)
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = true)
        }
    }

    override fun onListenStart(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            val characterState = getCharacterUIState(character.name)
            characterState.value = characterState.value.copy(
                activityState = CharacterActivityState.TO_INITIALIZE
            )
            charactersUIState.value = charactersUIState.value.copy(characterNames = getOrderedCharactersNames())
            ScriptInfoUIUtil.getScriptInfoUIState(character.name)
        }
    }

    override fun onListenStop(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            val characterState = getCharacterUIState(character.name)
            characterState.value = CharacterUIState(
                name = character.name,
                dofusClassId = character.dofusClassId,
                checked = false,
                activityState = CharacterActivityState.DISCONNECTED,
                entityLook = EntityLook(),
                skinImage = null
            )
            charactersUIState.value = charactersUIState.value.copy(
                characterNames = getOrderedCharactersNames()
            )
        }
    }

    override fun onLogsChange(logger: VldbLogger, logs: List<LogItem>) {
        LockUtils.executeSyncOperation(lock) {
            val characterName = characterNameByLogger[logger]
                ?: error("Unregistered logger character")
            val character = CharacterManager.getCharacter(characterName)
                ?: error("Character not found : $characterName")
            val loggerUIType = getCharacterLoggersWithTypes(character)[logger]
                ?: error("Unregistered logger type")
            val loggerUIState = LogsUIUtil.getLoggerUIState(characterName, loggerUIType)
            if (!loggerUIState.value.pauseLogs) {
                val autoScrollEnabled = loggerUIState.value.autoScroll
                val pauseEnabled = loggerUIState.value.pauseLogs
                val scrollState = if (autoScrollEnabled && !pauseEnabled) {
                    ScrollState(Int.MAX_VALUE)
                } else loggerUIState.value.scrollState
                loggerUIState.value = loggerUIState.value.copy(
                    logs = logs,
                    expandedLogItems = loggerUIState.value.expandedLogItems.toMutableList().also {
                        it.removeIf { logItem -> !logs.contains(logItem) }
                    },
                    scrollState = scrollState
                )
            }
        }
    }

    fun updateState(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            val characterUIState = getCharacterUIState(character.name)
            if (characterUIState.value.activityState != CharacterActivityState.BUSY) {
                computeState(character)
            }
        }
    }

    private fun computeState(character: DofusCharacter) {
        val connection = GameSnifferUtil.getFirstConnection(character)
        val newActivityState = getNewActivityState(connection)
        LockUtils.executeThreadedSyncOperation(lock) {
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
        return uiStateByCharacterName.values.filter { it.value.checked }
    }

    fun getSelectedCharacterUIState(): MutableState<CharacterUIState>? {
        return charactersUIState.value.selectedCharacterName?.let { getCharacterUIState(it) }
    }

    fun updateSkin(character: DofusCharacter, entityLook: EntityLook) {
        Thread {
            val characterUIState = getCharacterUIState(character.name).value
            val newSkins = SkinatorUtil.getRealEntityLook(entityLook).skins.toList()
            val currentSkins = SkinatorUtil.getRealEntityLook(characterUIState.entityLook).skins.toList()
            if (newSkins != currentSkins) {
                val newSkinImage = SkinatorRequestProcessor.getSkinImage(entityLook)
                LockUtils.executeSyncOperation(lock) {
                    val uiState = getCharacterUIState(character.name)
                    uiState.value = uiState.value.copy(
                        skinImage = newSkinImage,
                        entityLook = entityLook
                    )
                }
            }
        }.start()
    }

}