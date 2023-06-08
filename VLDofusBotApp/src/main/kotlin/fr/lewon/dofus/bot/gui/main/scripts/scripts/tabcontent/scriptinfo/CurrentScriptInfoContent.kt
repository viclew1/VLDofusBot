package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import kotlinx.coroutines.delay

@Composable
fun CurrentScriptInfoContent() {
    Column(Modifier.fillMaxWidth().height(150.dp)) {
        CommonText(
            "Running script(s)",
            modifier = Modifier.padding(4.dp),
            fontWeight = FontWeight.SemiBold
        )
        Box(Modifier.fillMaxSize().padding(5.dp).grayBoxStyle()) {
            val state = rememberScrollState()
            val characters = CharactersUIUtil.getSelectedCharactersUIStates().sortedBy { it.value.name }
            for (character in characters) {
                LaunchedEffect(character.value.name) {
                    while (true) {
                        ScriptInfoUIUtil.updateState(character.value.name)
                        delay(1000)
                    }
                }
            }
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
}