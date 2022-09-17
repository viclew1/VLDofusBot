package fr.lewon.dofus.bot.gui2.main.scripts.scripts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilders

object ScriptTabsUIUtil {

    val currentPage = mutableStateOf(ScriptTab.GLOBAL)
    val scripts = DofusBotScriptBuilders.values().map { it.buildScript() }
    private val uiState = ScriptTabUIState(ScriptTab.GLOBAL, scripts.first())
    private val currentScriptByCharacter = HashMap<DofusCharacter, MutableState<DofusBotScriptBuilder>>()
    private val currentGlobalScript = mutableStateOf(scripts.first())

    fun getCurrentScriptBuilder(): MutableState<DofusBotScriptBuilder> {
        return when (currentPage.value) {
            ScriptTab.INDIVIDUAL -> {
                val selectedCharacter = CharactersUIUtil.getSelectedCharacter()
                    ?: error("A character should be selected")
                currentScriptByCharacter.computeIfAbsent(selectedCharacter) {
                    mutableStateOf(scripts.first())
                }
            }
            ScriptTab.GLOBAL -> currentGlobalScript
        }
    }

    fun isScriptStarted(): Boolean {
        return CharactersUIUtil.getSelectedCharacters()
            .any { CharactersUIUtil.getCharacterUIState(it).value.runningScript != null }
    }

}