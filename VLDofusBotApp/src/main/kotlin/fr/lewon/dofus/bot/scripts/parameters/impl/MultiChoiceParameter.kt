package fr.lewon.dofus.bot.scripts.parameters.impl

import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class MultiChoiceParameter<T>(
    key: String,
    description: String,
    defaultValue: List<T>,
    parametersGroup: Int? = null,
    displayCondition: (parameterValues: ParameterValues) -> Boolean = { true },
    val getAvailableValues: (parameterValues: ParameterValues) -> List<T>,
    private val itemValueToString: (value: T) -> String,
    private val stringToItemValue: (str: String) -> T,
) : DofusBotParameter<List<T>>(key, description, defaultValue, parametersGroup, displayCondition) {

    override fun stringToValue(rawValue: String): List<T> =
        rawValue.split(MultiChoiceSeparator).map(stringToItemValue)

    override fun valueToString(value: List<T>): String =
        value.joinToString(MultiChoiceSeparator, transform = itemValueToString)
}

private const val MultiChoiceSeparator = "{__MULTI_CHOICE_SEPARATOR__}"