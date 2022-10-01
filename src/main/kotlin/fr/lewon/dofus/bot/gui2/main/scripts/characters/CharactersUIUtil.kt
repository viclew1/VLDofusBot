package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import fr.lewon.dofus.bot.core.utils.LockUtils
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LoggerUIType
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LogsUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo.ScriptInfoUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.selector.ScriptSelectorUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.network.NetworkAutoUpdater
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

object CharactersUIUtil : CharacterManagerListener, ScriptRunnerListener, GameSnifferListener, VldbLoggerListener {

    private val lock = ReentrantLock()
    private val uiStateByCharacter = HashMap<DofusCharacter, MutableState<CharacterUIState>>()
    private val charactersUIState = mutableStateOf(CharactersUIState(getOrderedCharacters()))
    private val characterByLogger = HashMap<VldbLogger, DofusCharacter>()

    fun initListeners() {
        CharacterManager.addListener(this)
        for (character in getAllCharacters()) {
            addListeners(character)
        }
        NetworkAutoUpdater.start()
    }

    private fun getOrderedCharacters(): List<DofusCharacter> {
        return CharacterManager.getCharacters().sortedWith(
            compareBy({ getCharacterUIState(it).value.activityState.displayOrder }, { it.pseudo })
        )
    }

    fun getCharacterUIState(character: DofusCharacter): MutableState<CharacterUIState> {
        return uiStateByCharacter.computeIfAbsent(character) {
            mutableStateOf(CharacterUIState(character))
        }
    }

    private fun addListeners(character: DofusCharacter) {
        ScriptRunner.addListener(character, this)
        GameSnifferUtil.addListener(character, this)
        characterByLogger[character.executionLogger] = character
        characterByLogger[character.snifferLogger] = character
        character.executionLogger.listeners.add(this)
        character.snifferLogger.listeners.add(this)
    }

    fun selectCharacter(character: DofusCharacter?) {
        LockUtils.executeSyncOperation(lock) {
            charactersUIState.value = charactersUIState.value.copy(selectedCharacter = character)
            ScriptTabsUIUtil.updateCurrentTab(if (character == null) ScriptTab.GLOBAL else ScriptTab.INDIVIDUAL)
        }
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            addListeners(character)
            charactersUIState.value = charactersUIState.value.copy(characters = getOrderedCharacters())
            getCharacterUIState(character)
        }
    }

    override fun onCharacterMove(character: DofusCharacter, toIndex: Int) {
        LockUtils.executeSyncOperation(lock) {
            charactersUIState.value = charactersUIState.value.copy(characters = getOrderedCharacters())
        }
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            uiStateByCharacter.remove(character)
            val charactersUIState = charactersUIState.value
            val newSelectedCharacter = if (charactersUIState.selectedCharacter == character) {
                null
            } else {
                charactersUIState.selectedCharacter
            }
            this.charactersUIState.value = charactersUIState.copy(
                characters = getOrderedCharacters(),
                selectedCharacter = newSelectedCharacter,
            )
            ScriptInfoUIUtil.removeScriptInfoUIState(character)
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
            val characterUIState = getCharacterUIState(character)
            characterUIState.value = characterUIState.value.copy(
                activityState = CharacterActivityState.BUSY,
                runningScript = script
            )
            ScriptInfoUIUtil.updateState(character)
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = true)
        }
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        LockUtils.executeSyncOperation(lock) {
            val characterState = getCharacterUIState(character)
            characterState.value = characterState.value.copy(runningScript = null)
            computeState(character)
            ScriptInfoUIUtil.updateState(character)
            ScriptSelectorUIUtil.uiState.value = ScriptSelectorUIUtil.uiState.value.copy(isStartButtonEnabled = true)
        }
    }

    override fun onListenStart(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            computeState(character, false)
        }
    }

    override fun onListenStop(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            computeState(character, false)
            val characterState = getCharacterUIState(character)
            characterState.value = characterState.value.copy(checked = false)
        }
    }

    override fun onLogsChange(logger: VldbLogger, logs: List<LogItem>) {
        LockUtils.executeSyncOperation(lock) {
            val character = characterByLogger[logger] ?: error("Unregistered logger character")
            val loggerUIType = getCharacterLoggersWithTypes(character)[logger]
                ?: error("Unregistered logger type")
            val loggerUIState = LogsUIUtil.getLoggerUIState(character, loggerUIType)
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
            val characterUIState = getCharacterUIState(character)
            if (characterUIState.value.activityState != CharacterActivityState.BUSY) {
                computeState(character)
            }
        }
    }

    private fun computeState(character: DofusCharacter, updateNetwork: Boolean = true) {
        GlobalScope.launch {
            if (updateNetwork) {
                GameSnifferUtil.updateNetwork()
            }
            doComputeState(character)
        }
    }

    private fun doComputeState(character: DofusCharacter) {
        val characterState = getCharacterUIState(character)
        val connection = GameSnifferUtil.getFirstConnection(character)
        val newActivityState = if (connection != null) {
            if (GameSnifferUtil.getGameInfoByConnection(connection).shouldInitBoard) {
                CharacterActivityState.TO_INITIALIZE
            } else {
                CharacterActivityState.AVAILABLE
            }
        } else {
            CharacterActivityState.DISCONNECTED
        }
        characterState.value = characterState.value.copy(activityState = newActivityState)
        charactersUIState.value = charactersUIState.value.copy(characters = getOrderedCharacters())
    }

    fun getAllCharacters(): List<DofusCharacter> {
        return charactersUIState.value.characters
    }

    fun getSelectedCharacters(): List<DofusCharacter> {
        return when (ScriptTabsUIUtil.getCurrentTab()) {
            ScriptTab.GLOBAL -> getCheckedCharacters()
            ScriptTab.INDIVIDUAL -> getSelectedCharacter()?.let { listOf(it) } ?: emptyList()
        }
    }

    fun getSelectedCharacter(): DofusCharacter? {
        return charactersUIState.value.selectedCharacter
    }

    private fun getCheckedCharacters(): List<DofusCharacter> {
        return uiStateByCharacter.entries.filter { it.value.value.checked }.map { it.key }
    }

}