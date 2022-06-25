package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import fr.lewon.dofus.bot.core.utils.LockUtils
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTab
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.ScriptTabsUIState
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LogsUIState
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.util.concurrent.locks.ReentrantLock

object CharactersUIState : CharacterManagerListener, ScriptRunnerListener, GameSnifferListener, VldbLoggerListener {

    val characters = mutableStateOf(CharacterManager.getCharacters())
    val selectedCharacter = mutableStateOf(characters.value.firstOrNull())
    val isDragging = mutableStateOf(false)
    private val stateByCharacter = HashMap<DofusCharacter, MutableState<CharacterActivityState>>()
    private val checkedByCharacter = HashMap<DofusCharacter, MutableState<Boolean>>()
    private val runningScriptByCharacter = HashMap<DofusCharacter, MutableState<DofusBotScriptBuilder?>>()
    private val characterByLogger = HashMap<VldbLogger, DofusCharacter>()
    private val loggerTypeByLogger = HashMap<VldbLogger, LogsUIState.LoggerType>()
    private val lock = ReentrantLock()

    init {
        CharacterManager.addListener(this)
        for (character in characters.value) {
            addListeners(character)
        }
    }

    private fun addListeners(character: DofusCharacter) {
        ScriptRunner.addListener(character, this)
        GameSnifferUtil.addListener(character, this)
        characterByLogger[character.executionLogger] = character
        characterByLogger[character.snifferLogger] = character
        loggerTypeByLogger[character.executionLogger] = LogsUIState.LoggerType.EXECUTION
        loggerTypeByLogger[character.snifferLogger] = LogsUIState.LoggerType.SNIFFER
        character.executionLogger.listeners.add(this)
        character.snifferLogger.listeners.add(this)
    }

    fun selectCharacter(character: DofusCharacter?) {
        LockUtils.executeSyncOperation(lock) {
            selectedCharacter.value = character
            ScriptTabsUIState.currentPage.value = if (character == null) ScriptTab.GLOBAL else ScriptTab.INDIVIDUAL
        }
    }

    fun getCharacterActivityState(character: DofusCharacter): MutableState<CharacterActivityState> {
        return LockUtils.executeSyncOperation(lock) {
            stateByCharacter.computeIfAbsent(character) { mutableStateOf(CharacterActivityState.DISCONNECTED) }
        }
    }

    fun getCharacterCheckedState(character: DofusCharacter): MutableState<Boolean> {
        return LockUtils.executeSyncOperation(lock) {
            checkedByCharacter.computeIfAbsent(character) { mutableStateOf(false) }
        }
    }

    fun getCharacterRunningScriptState(character: DofusCharacter): MutableState<DofusBotScriptBuilder?> {
        return LockUtils.executeSyncOperation(lock) {
            runningScriptByCharacter.computeIfAbsent(character) { mutableStateOf(null) }
        }
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            addListeners(character)
            characters.value = CharacterManager.getCharacters()
        }
    }

    override fun onCharacterMove(character: DofusCharacter, toIndex: Int) {
        LockUtils.executeSyncOperation(lock) {
            characters.value = CharacterManager.getCharacters()
        }
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            characters.value = CharacterManager.getCharacters()
            characterByLogger.remove(character.executionLogger)
            characterByLogger.remove(character.snifferLogger)
            loggerTypeByLogger.remove(character.executionLogger)
            loggerTypeByLogger.remove(character.snifferLogger)
            if (selectedCharacter.value == character) {
                selectCharacter(null)
            }
            checkedByCharacter[character]?.value = false
        }
    }

    override fun onScriptStart(character: DofusCharacter, script: DofusBotScriptBuilder) {
        LockUtils.executeSyncOperation(lock) {
            getCharacterActivityState(character).value = CharacterActivityState.BUSY
            getCharacterRunningScriptState(character).value = script
        }
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        LockUtils.executeSyncOperation(lock) {
            computeState(character)
            getCharacterRunningScriptState(character).value = null
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
            checkedByCharacter[character]?.value = false
        }
    }

    override fun onLogsChange(logger: VldbLogger, logs: List<LogItem>) {
        LockUtils.executeSyncOperation(lock) {
            val character = characterByLogger[logger] ?: error("Unregistered logger character")
            val loggerType = loggerTypeByLogger[logger] ?: error("Unregistered logger type")
            if (!LogsUIState.getPauseEnabled(character, loggerType).value) {
                LogsUIState.getLogs(character, loggerType).value = logs
                val expandedLogItems = LogsUIState.getExpandedLogItems(character, loggerType)
                expandedLogItems.value = expandedLogItems.value.toMutableList().also {
                    it.removeIf { logItem -> !logs.contains(logItem) }
                }
                val autoScrollEnabled = LogsUIState.getAutoScrollEnabled(character, loggerType).value
                val pauseEnabled = LogsUIState.getPauseEnabled(character, loggerType).value
                if (autoScrollEnabled && !pauseEnabled) {
                    LogsUIState.getScrollState(character, loggerType).value = ScrollState(Int.MAX_VALUE)
                }
            }
        }
    }

    fun updateState(character: DofusCharacter) {
        LockUtils.executeSyncOperation(lock) {
            if (getCharacterActivityState(character).value != CharacterActivityState.BUSY) {
                computeState(character)
            }
        }
    }

    private fun computeState(character: DofusCharacter, updateNetwork: Boolean = true) {
        Thread {
            val characterState = getCharacterActivityState(character)
            if (updateNetwork) {
                GameSnifferUtil.updateNetwork()
            }
            val connection = GameSnifferUtil.getFirstConnection(character)
            characterState.value = if (connection != null) {
                if (GameSnifferUtil.getGameInfoByConnection(connection).shouldInitBoard) {
                    CharacterActivityState.TO_INITIALIZE
                } else {
                    CharacterActivityState.AVAILABLE
                }
            } else {
                CharacterActivityState.DISCONNECTED
            }
        }.start()
    }

    fun getCheckedCharacters(): List<DofusCharacter> {
        return characters.value.filter { getCharacterCheckedState(it).value }
    }

}