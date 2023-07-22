package fr.lewon.dofus.bot.scripts.parameters

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues

abstract class DofusBotParameter<T>(
    val key: String,
    val description: String,
    val defaultValue: T,
    val parametersGroup: Int? = null,
    val displayCondition: (parameterValues: ParameterValues) -> Boolean = { true },
) {

    abstract fun stringToValue(rawValue: String): T

    abstract fun valueToString(value: T): String
}