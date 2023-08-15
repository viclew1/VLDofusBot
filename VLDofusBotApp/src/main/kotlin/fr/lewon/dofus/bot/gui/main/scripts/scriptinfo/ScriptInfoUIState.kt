package fr.lewon.dofus.bot.gui.main.scripts.scriptinfo

import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner

data class ScriptInfoUIState(
    val runningScript: ScriptRunner.RunningScript? = null,
    val text: String = FormatUtil.durationToStr(0),
)