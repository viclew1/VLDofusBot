package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.parameters

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

data class ScriptBuilderUIState(
    val scriptParameterUIStateByParameter: Map<DofusBotParameter, ScriptParameterUIState>
)