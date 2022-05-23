package fr.lewon.dofus.bot.scripts.parameters

open class DofusBotParameter(
    var key: String = "",
    var description: String = "",
    var value: String = "",
    var type: DofusBotParameterType = DofusBotParameterType.STRING,
    var possibleValues: List<String> = emptyList(),
    var defaultValue: String = value,
    var parentParameter: DofusBotParameter? = null,
    var displayCondition: () -> Boolean = { true }
)