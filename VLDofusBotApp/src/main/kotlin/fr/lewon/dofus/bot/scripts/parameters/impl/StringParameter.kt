package fr.lewon.dofus.bot.scripts.parameters.impl

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class StringParameter(
    key: String,
    description: String,
    defaultValue: String,
    parametersGroup: Int? = null,
    displayCondition: (parameterValues: ParameterValues) -> Boolean = { true },
) : DofusBotParameter<String>(key, description, defaultValue, parametersGroup, displayCondition) {

    override fun stringToValue(rawValue: String): String = rawValue

    override fun valueToString(value: String): String = value
}