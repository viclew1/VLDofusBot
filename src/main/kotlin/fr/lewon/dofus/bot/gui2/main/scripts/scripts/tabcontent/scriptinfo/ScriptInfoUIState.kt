package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import androidx.compose.ui.graphics.Color
import fr.lewon.dofus.bot.util.FormatUtil

data class ScriptInfoUIState(
    val text: String = FormatUtil.durationToStr(0),
    val color: Color = Color.White
)