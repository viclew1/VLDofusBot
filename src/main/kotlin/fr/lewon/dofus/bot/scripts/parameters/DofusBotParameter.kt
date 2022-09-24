package fr.lewon.dofus.bot.scripts.parameters

import fr.lewon.dofus.bot.model.characters.VldbScriptValues

data class DofusBotParameter(
    var key: String = "",
    var description: String = "",
    var defaultValue: String = "",
    var type: DofusBotParameterType = DofusBotParameterType.STRING,
    var possibleValues: List<String> = emptyList(),
    var displayCondition: (scriptValues: VldbScriptValues) -> Boolean = { true }
)