package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui2.util.AppColors

@Composable
fun CurrentScriptInfoContent() {
    Box(Modifier.fillMaxWidth().height(150.dp).padding(5.dp).grayBoxStyle()) {
        val state = rememberScrollState()
        val characters = CharactersUIUtil.getSelectedCharactersUIStates().sortedBy { it.value.name }
        Column(Modifier.fillMaxHeight().padding(end = 8.dp).verticalScroll(state)) {
            for (character in characters) {
                ScriptInfoContent(character.value.name)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                .background(AppColors.backgroundColor),
            adapter = rememberScrollbarAdapter(state),
        )
    }
}