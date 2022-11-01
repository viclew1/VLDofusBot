package fr.lewon.dofus.bot.gui2.main.scripts.scripts

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters.ScriptParametersUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.selector.ScriptSelectorUIUtil
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilders
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.script.ScriptRunner

object ScriptTabsUIUtil {

    val scripts = DofusBotScriptBuilders.values().map { it.builder }
    private val uiState = mutableStateOf(ScriptTabUIState(ScriptTab.GLOBAL, scripts.first()))

    fun getCurrentScriptBuilder(): DofusBotScriptBuilder {
        return when (uiState.value.currentTab) {
            ScriptTab.INDIVIDUAL ->
                CharactersUIUtil.getSelectedCharacterUIState()?.value?.scriptBuilder
                    ?: error("A character should be selected")
            ScriptTab.GLOBAL ->
                uiState.value.globalScriptBuilder
        }
    }

    fun updateCurrentScriptBuilder(scriptBuilder: DofusBotScriptBuilder) {
        when (uiState.value.currentTab) {
            ScriptTab.INDIVIDUAL -> {
                val characterUIState = CharactersUIUtil.getSelectedCharacterUIState()
                    ?: error("A character should be selected")
                characterUIState.value = characterUIState.value.copy(scriptBuilder = scriptBuilder)
            }
            ScriptTab.GLOBAL -> uiState.value = uiState.value.copy(globalScriptBuilder = scriptBuilder)
        }
    }

    fun isScriptStarted(): Boolean {
        return CharactersUIUtil.getSelectedCharactersUIStates()
            .any { it.value.runningScript != null }
    }

    fun getCurrentTab(): ScriptTab {
        return uiState.value.currentTab
    }

    fun updateCurrentTab(scriptTab: ScriptTab) {
        uiState.value = uiState.value.copy(currentTab = scriptTab)
    }

    fun toggleScript() {
        val isStarted = isScriptStarted()
        val uiState = ScriptSelectorUIUtil.uiState.value
        val selectedCharactersUIStates = CharactersUIUtil.getSelectedCharactersUIStates()
        Thread {
            ScriptSelectorUIUtil.uiState.value = uiState.copy(isStartButtonEnabled = false)
            val selectedCharactersNames = selectedCharactersUIStates.map { it.value.name }
            val selectedCharacters = CharacterManager.getCharacters(selectedCharactersNames)
            if (isStarted) {
                selectedCharacters.forEach { ScriptRunner.stopScript(it) }
            } else {
                val scriptBuilder = getCurrentScriptBuilder()
                val scriptValues = ScriptParametersUIUtil.getScriptValuesStore().getValues(scriptBuilder)
                selectedCharacters.forEach { ScriptRunner.runScript(it, scriptBuilder, scriptValues) }
            }
        }.start()
    }

}