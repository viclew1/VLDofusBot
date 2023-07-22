package fr.lewon.dofus.bot.gui.main.scripts.scripts.tabcontent.parameters

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder

data class ScriptParametersUiState(
    val parameterValuesByScript: Map<DofusBotScriptBuilder, ParameterValues> = emptyMap(),
)