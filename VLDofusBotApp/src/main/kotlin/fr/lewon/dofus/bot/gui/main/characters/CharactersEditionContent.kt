package fr.lewon.dofus.bot.gui.main.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.CustomStyledColumn
import fr.lewon.dofus.bot.gui.custom.HorizontalSeparator
import fr.lewon.dofus.bot.gui.main.characters.edit.global.CharacterGlobalDisplayContent
import fr.lewon.dofus.bot.gui.main.characters.edit.parameters.CharacterGeneralParametersContent
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.CharacterSetsContent
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.CharacterSetsUiUtil
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar.items.CharacterItemsEditionContent
import fr.lewon.dofus.bot.gui.main.characters.edit.sets.bar.spells.CharacterSpellsEditionContent
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun CharactersEditionContent() {
    Row {
        Row(Modifier.width(180.dp)) {
            val characterUIStates = CharactersUIUtil.getAllCharacterUIStates()
            CharactersListContent(
                characterUiStates = characterUIStates,
                canSelectMultipleCharacters = false,
                emptyMessage = "Log your characters in to the game to create their bot profile."
            )
        }
        val selectedCharacterUiStates = CharactersUIUtil.getSelectedCharactersUIStates()
        if (selectedCharacterUiStates.isEmpty()) {
            CommonText(
                "Select a character to edit it.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 20.dp)
            )
        } else {
            val characterUIState = selectedCharacterUiStates.first()
            Column(Modifier.fillMaxSize().weight(1f)) {
                Row(Modifier.height(400.dp)) {
                    Row(Modifier.width(180.dp).padding(5.dp)) {
                        CharacterGlobalDisplayContent(characterUIState)
                    }
                    CharacterSetsContent(characterUIState)
                }
                Column(Modifier.fillMaxSize().weight(1f).padding(5.dp)) {
                    CharacterGeneralParametersContent(characterUIState)
                }
            }
            Column(Modifier.width(400.dp).fillMaxHeight()) {
                CustomStyledColumn(
                    "Selected set : ${CharacterSetsUiUtil.getCurrentSet(characterUIState.name).name}",
                    Modifier.fillMaxSize().padding(5.dp)
                ) {
                    Spacer(Modifier.height(10.dp))
                    HorizontalSeparator("Spells")
                    Column(Modifier.padding(5.dp).fillMaxSize().weight(1f)) {
                        CharacterSpellsEditionContent(characterUIState)
                    }
                    HorizontalSeparator("Items")
                    Column(Modifier.padding(5.dp).fillMaxSize().weight(1f)) {
                        Box(Modifier.fillMaxSize()) { //TODO : remove box when ready
                            CharacterItemsEditionContent(characterUIState)
                            Box(Modifier.fillMaxSize().background(AppColors.RED.copy(alpha = 0.2f))) {
                                CommonText(
                                    "DOES NOT WORK YET,\nWORK IN PROGRESS",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}