package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CharactersListContent() {
    val characters = CharactersUIUtil.getAllCharacterUIStates()
    val selectedCharacter = CharactersUIUtil.getSelectedCharacterUIState()
    Column {
        HeaderLine()
        if (characters.isEmpty()) {
            Box(Modifier.padding(horizontal = 5.dp, vertical = 20.dp)) {
                CommonText(
                    "Log your characters in to the game to create their bot profile.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            Box {
                val state = rememberScrollState()
                Column(Modifier.fillMaxHeight().padding(end = 8.dp).verticalScroll(state)) {
                    for (character in characters) {
                        Column(Modifier.height(30.dp)) {
                            CharacterCardContent(character, selectedCharacter == character)
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                        .background(AppColors.backgroundColor),
                    adapter = rememberScrollbarAdapter(state),
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun HeaderLine() {
    Row(Modifier.fillMaxWidth().height(30.dp)) {
        CommonText(
            "Characters",
            modifier = Modifier.padding(4.dp).align(Alignment.CenterVertically),
            fontWeight = FontWeight.SemiBold
        )
    }
}