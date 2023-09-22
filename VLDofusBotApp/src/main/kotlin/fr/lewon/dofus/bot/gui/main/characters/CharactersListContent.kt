package fr.lewon.dofus.bot.gui.main.characters

import androidx.compose.runtime.Composable
import fr.lewon.dofus.bot.gui.custom.list.CustomListContent
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

@Composable
fun CharactersListContent(
    characterUiStates: List<CharacterUIState>,
    canSelectMultipleCharacters: Boolean,
    emptyMessage: String,
) {
    val selectedCharacterUiStates = CharactersUIUtil.getSelectedCharactersUIStates().sortedBy { it.name }
    CustomListContent(
        title = "Characters",
        emptyMessage = emptyMessage,
        selectedItems = selectedCharacterUiStates,
        allItems = characterUiStates,
        canDeleteItem = { it.activityState == CharacterActivityState.DISCONNECTED },
        canSelectMultipleItems = canSelectMultipleCharacters,
        onSelect = {
            CharactersUIUtil.selectCharacters(it.map { characterUIState -> characterUIState.name })
        },
        onDelete = { CharacterManager.removeCharacter(it.name) },
        itemCardMainContent = { item, textColor -> CharacterCardContent(item, textColor) }
    )
}