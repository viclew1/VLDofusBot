package fr.lewon.dofus.bot.gui.main.characters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.CustomStyledColumn
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.custom.handPointerIcon
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.util.AppColors

@Composable
fun CharactersListContent(
    characterUiStates: List<CharacterUIState>,
    canSelectMultipleCharacters: Boolean,
    emptyMessage: String,
) {
    val selectedCharacterUiStates = CharactersUIUtil.getSelectedCharactersUIStates().sortedBy { it.name }
    val allSelected = remember { mutableStateOf(false) }
    selectedCharacterUiStates.filter { it !in characterUiStates }.forEach {
        CharactersUIUtil.toggleSelect(it.name)
    }
    if (!canSelectMultipleCharacters && selectedCharacterUiStates.size > 1) {
        val toSelectCharacterUiState = characterUiStates.firstOrNull { it in selectedCharacterUiStates }
        if (toSelectCharacterUiState != null) {
            CharactersUIUtil.selectCharacter(toSelectCharacterUiState.name)
        } else {
            CharactersUIUtil.unselectAllCharacters()
        }
    }
    if (selectedCharacterUiStates.isEmpty()) {
        allSelected.value = false
    } else if (selectedCharacterUiStates.size == characterUiStates.size) {
        allSelected.value = true
    }
    CustomStyledColumn(headerContent = {
        Row(Modifier.height(30.dp)) {
            CommonText(
                "Characters",
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.fillMaxWidth().weight(1f))
            if (canSelectMultipleCharacters) {
                TooltipTarget(if (allSelected.value) "Unselect all" else "Select all") {
                    Checkbox(allSelected.value, { newValue ->
                        allSelected.value = newValue
                        if (newValue) {
                            CharactersUIUtil.selectCharacters(characterUiStates.map { it.name })
                        } else {
                            CharactersUIUtil.unselectAllCharacters()
                        }
                    }, modifier = Modifier.handPointerIcon())
                }
            }
        }
    }, Modifier.padding(5.dp).grayBoxStyle()) {
        if (characterUiStates.isEmpty()) {
            Box(Modifier.padding(horizontal = 5.dp, vertical = 20.dp)) {
                CommonText(
                    emptyMessage,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            Box {
                val state = rememberScrollState()
                Column(Modifier.fillMaxHeight().padding(end = 8.dp).verticalScroll(state)) {
                    for (characterUiState in characterUiStates) {
                        Column(Modifier.height(30.dp)) {
                            CharacterCardContent(
                                characterUiState,
                                characterUiState in selectedCharacterUiStates,
                                canSelectMultipleCharacters
                            )
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).padding(1.dp).align(Alignment.CenterEnd)
                        .background(AppColors.backgroundColor),
                    adapter = rememberScrollbarAdapter(state),
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}