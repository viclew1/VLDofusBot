package fr.lewon.dofus.bot.gui2.main.scripts.scripts

import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder

data class ScriptTabUIState(
    val currentPage: ScriptTab,
    val globalScript: DofusBotScriptBuilder
)