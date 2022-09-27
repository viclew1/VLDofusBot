package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.model.characters.DofusCharacter

@Composable
fun ScriptInfoContent(character: DofusCharacter) {
    val scriptInfoUIState = ScriptInfoUIUtil.getScriptInfoUIState(character)
    CommonText(
        scriptInfoUIState.value.text,
        modifier = Modifier.padding(4.dp),
        fontWeight = FontWeight.SemiBold,
        enabledColor = scriptInfoUIState.value.color
    )
}