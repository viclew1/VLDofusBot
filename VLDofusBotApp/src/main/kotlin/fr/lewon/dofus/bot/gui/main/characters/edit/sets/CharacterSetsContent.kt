package fr.lewon.dofus.bot.gui.main.characters.edit.sets

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.SubTitleText
import fr.lewon.dofus.bot.gui.custom.list.CustomListContent
import fr.lewon.dofus.bot.gui.main.characters.CharacterUIState
import fr.lewon.dofus.bot.model.characters.sets.CharacterSet
import fr.lewon.dofus.bot.model.characters.sets.CharacterSets
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSetsManager

@Composable
fun CharacterSetsContent(characterUIState: CharacterUIState) {
    val characterSets = CharacterSetsUiUtil.getUiStateValue().setsByCharacterName[characterUIState.name]
        ?: CharacterSets(characterUIState.name)
    val selectedSet = characterSets.getSelectedSet()
    CustomListContent(
        title = "Character Sets",
        emptyMessage = "No character set available",
        selectedItems = selectedSet?.let { listOf(it) } ?: emptyList(),
        allItems = characterSets.sets,
        canDeleteItem = { it.name != CharacterSetsManager.DefaultSetName },
        canSelectMultipleItems = false,
        onSelect = {
            it.firstOrNull()?.let { set ->
                CharacterSetsManager.setSelectedSet(characterUIState.name, set.name)
            }
        },
        onDelete = { CharacterSetsManager.deleteSet(characterUIState.name, it.name) },
        canCreateItem = true,
        onCreate = { CharacterSetsManager.updateSet(characterUIState.name, CharacterSet(it)) },
        createItemPlaceHolder = "New set name",
        createItemButtonText = "Create Set",
        itemCardMainContent = { set: CharacterSet, textColor: Color ->
            SubTitleText(
                set.name,
                Modifier.align(Alignment.CenterVertically).padding(start = 5.dp),
                maxLines = 1,
                enabledColor = textColor
            )
        }
    )
}