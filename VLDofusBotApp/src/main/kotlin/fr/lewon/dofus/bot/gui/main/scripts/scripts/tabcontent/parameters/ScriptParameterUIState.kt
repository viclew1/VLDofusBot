package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.parameters

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

data class ScriptParameterUIState(
    val parameter: DofusBotParameter,
    val displayed: Boolean,
    val parameterValue: String
)