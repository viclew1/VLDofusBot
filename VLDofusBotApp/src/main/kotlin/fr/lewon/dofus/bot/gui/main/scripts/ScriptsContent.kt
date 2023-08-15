package fr.lewon.dofus.bot.gui.main.scripts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CustomStyledColumn
import fr.lewon.dofus.bot.gui.main.characters.CharacterActivityState
import fr.lewon.dofus.bot.gui.main.characters.CharactersListContent
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.character.SelectedCharacterDisplayContent
import fr.lewon.dofus.bot.gui.main.scripts.logs.LogsCharacterTabContent

@Composable
fun ScriptsContent() {
    Row(Modifier.fillMaxSize()) {
        Row(Modifier.width(180.dp)) {
            val connectedCharacters = CharactersUIUtil.getAllCharacterUIStates().filter { it.activityState != CharacterActivityState.DISCONNECTED }
            CharactersListContent(
                characterUiStates = connectedCharacters,
                canSelectMultipleCharacters = true,
                emptyMessage = "Log your characters in to the game to use them in scripts."
            )
        }
        Column(Modifier.weight(1f)) {
            ScriptPanelContent()
        }
        Column(Modifier.fillMaxHeight().width(525.dp)) {
            val selectedCharacterUiStates = CharactersUIUtil.getSelectedCharactersUIStates()
            AnimatedVisibility(
                visible = selectedCharacterUiStates.isNotEmpty(),
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top),
            ) {
                CustomStyledColumn(
                    "Selected characters",
                    modifier = Modifier.heightIn(min = 10.dp, max = 430.dp).padding(5.dp)
                ) {
                    Box(Modifier.fillMaxWidth().padding(top = 5.dp).padding(horizontal = 5.dp)) {
                        val scrollState = rememberScrollState()
                        val scrollBarHeight = remember { mutableStateOf(0.dp) }
                        Column(Modifier.padding(end = 10.dp).verticalScroll(scrollState).onGloballyPositioned {
                            scrollBarHeight.value = it.size.height.dp
                        }) {
                            for (selectedCharacterUiState in selectedCharacterUiStates) {
                                SelectedCharacterDisplayContent(selectedCharacterUiState)
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.width(8.dp).height(scrollBarHeight.value).align(Alignment.CenterEnd),
                            adapter = rememberScrollbarAdapter(scrollState),
                        )
                    }
                }
            }
            Row(Modifier.fillMaxHeight()) {
                LogsCharacterTabContent()
            }
        }
    }
}