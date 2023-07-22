package fr.lewon.dofus.bot.scripts.parameters.impl

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class LongParameter(
    key: String,
    description: String,
    defaultValue: Long,
    parametersGroup: Int? = null,
    displayCondition: (parameterValues: ParameterValues) -> Boolean = { true },
) : DofusBotParameter<Long>(key, description, defaultValue, parametersGroup, displayCondition) {

    override fun stringToValue(rawValue: String): Long = rawValue.toLong()

    override fun valueToString(value: Long): String = value.toString()
}