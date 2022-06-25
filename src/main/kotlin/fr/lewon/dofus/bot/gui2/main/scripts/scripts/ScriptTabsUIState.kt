package fr.lewon.dofus.bot.gui2.main.scripts.scripts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIState
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilders

object ScriptTabsUIState {

    val currentPage = mutableStateOf(ScriptTab.GLOBAL)
    val scripts = DofusBotScriptBuilders.values().map { it.buildScript() }
    private val currentScriptByCharacter = HashMap<DofusCharacter, MutableState<DofusBotScriptBuilder>>()
    private val currentGlobalScript = mutableStateOf(scripts.first())

    fun getCurrentCharacter(): DofusCharacter {
        return CharactersUIState.selectedCharacter.value
            ?: error("A character should be selected")
    }

    fun getCurrentScriptBuilder(): MutableState<DofusBotScriptBuilder> {
        return when (currentPage.value) {
            ScriptTab.INDIVIDUAL -> currentScriptByCharacter.computeIfAbsent(getCurrentCharacter()) {
                mutableStateOf(scripts.first())
            }
            ScriptTab.GLOBAL -> currentGlobalScript
        }
    }

    fun isScriptStarted(): Boolean {
        return getSelectedCharacters().any { CharactersUIState.getCharacterRunningScriptState(it).value != null }
    }

    fun getSelectedCharacters(): List<DofusCharacter> {
        return when (currentPage.value) {
            ScriptTab.GLOBAL -> CharactersUIState.getCheckedCharacters()
            ScriptTab.INDIVIDUAL -> listOf(getCurrentCharacter())
        }
    }

}