package fr.lewon.dofus.bot.scripts.parameters

import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValues

data class DofusBotParameter(
    var key: String = "",
    var description: String = "",
    var defaultValue: String = "",
    var type: DofusBotParameterType = DofusBotParameterType.STRING,
    var possibleValues: List<String> = emptyList(),
    var parametersGroup: Int? = null,
    var displayCondition: (scriptValues: ScriptValues) -> Boolean = { true },
)