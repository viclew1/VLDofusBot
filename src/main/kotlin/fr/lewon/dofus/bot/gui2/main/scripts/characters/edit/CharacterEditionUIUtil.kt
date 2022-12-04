package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.gui2.ComposeUIUtil
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil

object CharacterEditionUIUtil : ComposeUIUtil() {

    private val uiState = mutableStateOf(CharacterEditionUIState())

    fun getEditedCharacterUIState(): MutableState<CharacterUIState>? {
        val characterName = uiState.value.characterName ?: return null
        return CharactersUIUtil.getCharacterUIState(characterName)
    }

    fun editCharacter(characterName: String) {
        uiState.value = uiState.value.copy(characterName = characterName)
    }

    fun getEditionTab(): EditionTab {
        return uiState.value.editionTab
    }

    fun updateEditionTab(editionTab: EditionTab) {
        uiState.value = uiState.value.copy(editionTab = editionTab)
    }

}