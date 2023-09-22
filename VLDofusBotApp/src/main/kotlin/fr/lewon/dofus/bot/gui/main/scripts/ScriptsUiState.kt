package fr.lewon.dofus.bot.gui.main.scripts

import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilders

data class ScriptsUiState(
    val currentScriptBuilder: DofusBotScriptBuilder = DofusBotScriptBuilders.entries.first().builder,
)