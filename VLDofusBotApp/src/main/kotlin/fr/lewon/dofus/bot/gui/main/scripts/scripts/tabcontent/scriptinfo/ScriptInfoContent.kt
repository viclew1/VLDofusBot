package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui.custom.CommonText

@Composable
fun ScriptInfoContent(characterName: String) {
    val scriptInfoUIState = ScriptInfoUIUtil.getScriptInfoUIState(characterName)
    val color = if (scriptInfoUIState.value.runningScript) Color.White else Color.Green
    CommonText(
        scriptInfoUIState.value.text,
        modifier = Modifier.padding(4.dp),
        fontWeight = FontWeight.SemiBold,
        enabledColor = color
    )
}