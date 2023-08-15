package fr.lewon.dofus.bot.gui.main.scripts.scriptinfo

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText
import fr.lewon.dofus.bot.gui.custom.darkGrayBoxStyle
import fr.lewon.dofus.bot.gui.custom.grayBoxStyle
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.gui.main.scripts.ScriptsUiUtil
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.script.ScriptRunner
import kotlinx.coroutines.delay

@Composable
fun CurrentScriptInfoContent() {
    Column(Modifier.fillMaxWidth().height(165.dp).padding(5.dp).grayBoxStyle()) {
        if (ScriptsUiUtil.isScriptStarted()) {
            Row(Modifier.fillMaxWidth().height(25.dp).darkGrayBoxStyle().padding(5.dp)) {
                CommonText(
                    "Running script(s)",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontWeight = FontWeight.SemiBold
                )
            }
            val state = rememberScrollState()
            val selectedCharactersUiStates = CharactersUIUtil.getSelectedCharactersUIStates().sortedBy { it.name }
            for (character in selectedCharactersUiStates) {
                LaunchedEffect(character.name) {
                    while (true) {
                        ScriptInfoUIUtil.updateState(character.name)
                        delay(1000)
                    }
                }
            }
            Row(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().weight(1f)) {
                    for (character in selectedCharactersUiStates) {
                        ScriptInfoContent(character.name)
                    }
                }
                if (selectedCharactersUiStates.size == 1) {
                    val runningScript = selectedCharactersUiStates.firstOrNull()?.name?.let { characterName ->
                        ScriptInfoUIUtil.getScriptInfoUIState(characterName).value.runningScript
                    }
                    if (runningScript != null && runningScript.stats.isNotEmpty()) {
                        Box(
                            Modifier.padding(vertical = 8.dp)
                                .border(BorderStroke(1.dp, Color.DarkGray))
                                .width(1.dp)
                                .fillMaxHeight()
                        ) {}
                        Box(Modifier.fillMaxHeight().width(200.dp)) {
                            Column(Modifier.verticalScroll(state).padding(end = 10.dp)) {
                                ScriptExecutionStatsContent(runningScript)
                            }
                            VerticalScrollbar(
                                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd)
                                    .background(AppColors.backgroundColor),
                                adapter = rememberScrollbarAdapter(state),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScriptExecutionStatsContent(runningScript: ScriptRunner.RunningScript) {
    Column {
        for (stat in runningScript.stats) {
            Row(Modifier.padding(5.dp)) {
                CommonText(
                    stat.key.key,
                    modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterVertically)
                )
                CommonText(
                    stat.value,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}