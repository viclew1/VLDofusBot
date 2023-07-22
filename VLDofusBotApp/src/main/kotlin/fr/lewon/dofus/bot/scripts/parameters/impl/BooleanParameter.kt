package fr.lewon.dofus.bot.scripts.parameters.impl

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class BooleanParameter(
    key: String,
    description: String,
    defaultValue: Boolean,
    parametersGroup: Int? = null,
    displayCondition: (parameterValues: ParameterValues) -> Boolean = { true },
) : DofusBotParameter<Boolean>(key, description, defaultValue, parametersGroup, displayCondition) {

    override fun stringToValue(rawValue: String): Boolean = rawValue.toBoolean()

    override fun valueToString(value: Boolean): String = value.toString()
}