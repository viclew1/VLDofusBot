package fr.lewon.dofus.bot.gui2.main.scripts.scripts

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilders

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

}