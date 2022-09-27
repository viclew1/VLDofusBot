package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import kotlinx.coroutines.delay

@Composable
fun CurrentScriptInfoContent() {
    LaunchedEffect(Unit) {
        while (true) {
            for (character in CharactersUIUtil.getSelectedCharacters()) {
                ScriptInfoUIUtil.updateState(character)
            }
            delay(1000)
        }
    }
    Box(
        Modifier.fillMaxWidth().height(150.dp).padding(5.dp)
            .border(BorderStroke(1.dp, Color.LightGray))
            .background(AppColors.DARK_BG_COLOR)
    ) {
        val lazyListState = rememberLazyListState()
        val characters = CharactersUIUtil.getSelectedCharacters().sortedBy(DofusCharacter::pseudo)
        LazyColumn(Modifier.fillMaxHeight().padding(end = 8.dp), state = lazyListState) {
            items(characters.size) {
                val character = characters[it]
                ScriptInfoUIUtil.updateState(character)
                ScriptInfoContent(character)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                .background(AppColors.backgroundColor),
            adapter = rememberScrollbarAdapter(lazyListState),
        )
    }
}