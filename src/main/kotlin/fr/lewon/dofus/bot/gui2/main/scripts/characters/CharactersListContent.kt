package fr.lewon.dofus.bot.gui2.main.scripts.characters

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.AnimatedButton
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.CustomShapes
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CharactersListContent() {
    val characters = CharactersUIUtil.getAllCharacters()
    val selectedCharacter = CharactersUIUtil.getSelectedCharacter()
    Column {
        HeaderLine()
        Box {
            val lazyListState = rememberLazyListState()
            Column(Modifier.fillMaxHeight().padding(end = 8.dp)) {
                for (character in characters) {
                    Column(Modifier.height(30.dp)) {
                        CharacterCardContent(character, selectedCharacter == character)
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                    .background(AppColors.backgroundColor),
                adapter = rememberScrollbarAdapter(lazyListState),
            )
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
        Spacer(Modifier.weight(1f))
        AnimatedButton(
            { println("Add new character") }, "Add", Icons.Default.Add,
            modifier = Modifier.width(50.dp).fillMaxHeight(),
            shape = CustomShapes.buildTrapezoidShape(topLeftDeltaRatio = 0.15f)
        )
    }
}