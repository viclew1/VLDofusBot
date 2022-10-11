package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.scriptinfo

import fr.lewon.dofus.bot.util.FormatUtil

data class ScriptInfoUIState(
    val runningScript: Boolean = false,
    val text: String = FormatUtil.durationToStr(0),
)