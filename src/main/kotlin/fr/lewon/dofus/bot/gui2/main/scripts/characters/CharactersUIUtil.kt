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
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs.LogsUIState
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import fr.lewon.dofus.bot.util.network.GameSnifferListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.util.concurrent.locks.ReentrantLock

object CharactersUIUtil : CharacterManagerListener, ScriptRunnerListener, GameSnifferListener, VldbLoggerListener {

    private val LOCK = ReentrantLock()
    private val UI_STATE_BY_CHARACTER = HashMap<DofusCharacter, MutableState<CharacterUIState>>()
    private val CHARACTERS_UI_STATE = mutableStateOf(CharactersUIState(getOrderedCharacters()))
    private val CHARACTER_BY_LOGGER = HashMap<VldbLogger, DofusCharacter>()

    init {
        for (character in getOrderedCharacters()) {
            addListeners(character)
        }
    }

    private fun getOrderedCharacters(): List<DofusCharacter> {
        return CharacterManager.getCharacters().sortedWith(
            compareBy({ getCharacterUIState(it).value.activityState.displayOrder }, { it.pseudo })
        )
    }

    fun getCharacterUIState(character: DofusCharacter): MutableState<CharacterUIState> {
        var characterUIState = UI_STATE_BY_CHARACTER[character]
        if (characterUIState == null) {
            characterUIState = mutableStateOf(CharacterUIState(character))
            UI_STATE_BY_CHARACTER[character] = characterUIState
        }
        return UI_STATE_BY_CHARACTER.computeIfAbsent(character) { mutableStateOf(CharacterUIState(it)) }
    }

    private fun addListeners(character: DofusCharacter) {
        ScriptRunner.addListener(character, this)
        GameSnifferUtil.addListener(character, this)
        CHARACTER_BY_LOGGER[character.executionLogger] = character
        CHARACTER_BY_LOGGER[character.snifferLogger] = character
        character.executionLogger.listeners.add(this)
        character.snifferLogger.listeners.add(this)
    }

    fun selectCharacter(character: DofusCharacter?) {
        LockUtils.executeSyncOperation(LOCK) {
            CHARACTERS_UI_STATE.value = CHARACTERS_UI_STATE.value.copy(selectedCharacter = character)
            ScriptTabsUIUtil.currentPage.value = if (character == null) ScriptTab.GLOBAL else ScriptTab.INDIVIDUAL
        }
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        LockUtils.executeSyncOperation(LOCK) {
            addListeners(character)
            CHARACTERS_UI_STATE.value = CHARACTERS_UI_STATE.value.copy(characters = getOrderedCharacters())
            getCharacterUIState(character)
        }
    }

    override fun onCharacterMove(character: DofusCharacter, toIndex: Int) {
        LockUtils.executeSyncOperation(LOCK) {
            CHARACTERS_UI_STATE.value = CHARACTERS_UI_STATE.value.copy(characters = getOrderedCharacters())
        }
    }

    override fun onCharacterUpdate(character: DofusCharacter) {
        LockUtils.executeSyncOperation(LOCK) {
            CHARACTERS_UI_STATE.value = CHARACTERS_UI_STATE.value.copy(characters = getOrderedCharacters())
            computeState(character, true)
        }
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        LockUtils.executeSyncOperation(LOCK) {
            UI_STATE_BY_CHARACTER.remove(character)
            val charactersUIState = CHARACTERS_UI_STATE.value
            val newSelectedCharacter = if (charactersUIState.selectedCharacter == character) {
                null
            } else {
                charactersUIState.selectedCharacter
            }
            CHARACTERS_UI_STATE.value = charactersUIState.copy(
                characters = getOrderedCharacters(),
                selectedCharacter = newSelectedCharacter,
            )
        }
    }

    private fun getCharacterLoggersWithTypes(character: DofusCharacter): Map<VldbLogger, LoggerUIType> {
        return mapOf(
            character.snifferLogger to LoggerUIType.SNIFFER,
            character.executionLogger to LoggerUIType.EXECUTION
        )
    }

    override fun onScriptStart(character: DofusCharacter, script: ScriptRunner.RunningScript) {
        LockUtils.executeSyncOperation(LOCK) {
            val characterUIState = getCharacterUIState(character)
            characterUIState.value = characterUIState.value.copy(
                activityState = CharacterActivityState.BUSY,
                runningScript = script
            )
        }
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        LockUtils.executeSyncOperation(LOCK) {
            val characterState = getCharacterUIState(character)
            characterState.value = characterState.value.copy(runningScript = null)
            computeState(character)
        }
    }

    override fun onListenStart(character: DofusCharacter) {
        LockUtils.executeSyncOperation(LOCK) {
            computeState(character, false)
        }
    }

    override fun onListenStop(character: DofusCharacter) {
        LockUtils.executeSyncOperation(LOCK) {
            computeState(character, false)
            val characterState = getCharacterUIState(character)
            characterState.value = characterState.value.copy(checked = false)
        }
    }

    override fun onLogsChange(logger: VldbLogger, logs: List<LogItem>) {
        LockUtils.executeSyncOperation(LOCK) {
            val character = CHARACTER_BY_LOGGER[logger] ?: error("Unregistered logger character")
            val loggerUIType = getCharacterLoggersWithTypes(character)[logger]
                ?: error("Unregistered logger type")
            if (!LogsUIState.getPauseEnabled(character, loggerUIType).value) {
                LogsUIState.getLogs(character, loggerUIType).value = logs
                val expandedLogItems = LogsUIState.getExpandedLogItems(character, loggerUIType)
                expandedLogItems.value = expandedLogItems.value.toMutableList().also {
                    it.removeIf { logItem -> !logs.contains(logItem) }
                }
                val autoScrollEnabled = LogsUIState.getAutoScrollEnabled(character, loggerUIType).value
                val pauseEnabled = LogsUIState.getPauseEnabled(character, loggerUIType).value
                if (autoScrollEnabled && !pauseEnabled) {
                    LogsUIState.getScrollState(character, loggerUIType).value = ScrollState(Int.MAX_VALUE)
                }
            }
        }
    }

    fun updateState(character: DofusCharacter) {
        LockUtils.executeSyncOperation(LOCK) {
            val characterUIState = getCharacterUIState(character)
            if (characterUIState.value.activityState != CharacterActivityState.BUSY) {
                computeState(character)
            }
        }
    }

    private fun computeState(character: DofusCharacter, updateNetwork: Boolean = true) {
        Thread {
            val characterState = getCharacterUIState(character)
            if (updateNetwork) {
                GameSnifferUtil.updateNetwork()
            }
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
            CHARACTERS_UI_STATE.value = CHARACTERS_UI_STATE.value.copy(characters = getOrderedCharacters())
        }.start()
    }

    fun getAllCharacters(): List<DofusCharacter> {
        return CHARACTERS_UI_STATE.value.characters
    }

    fun getSelectedCharacters(): List<DofusCharacter> {
        return when (ScriptTabsUIUtil.currentPage.value) {
            ScriptTab.GLOBAL -> getCheckedCharacters()
            ScriptTab.INDIVIDUAL -> getSelectedCharacter()?.let { listOf(it) } ?: emptyList()
        }
    }

    fun getSelectedCharacter(): DofusCharacter? {
        return CHARACTERS_UI_STATE.value.selectedCharacter
    }

    private fun getCheckedCharacters(): List<DofusCharacter> {
        return UI_STATE_BY_CHARACTER.entries.filter { it.value.value.checked }.map { it.key }
    }

}