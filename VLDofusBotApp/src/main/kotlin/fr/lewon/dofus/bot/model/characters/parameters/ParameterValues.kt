package fr.lewon.dofus.bot.model.characters.parameters

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class ParameterValues : HashMap<String, String>() {

    fun <T> getParamValue(parameter: DofusBotParameter<T>): T {
        return this[parameter.key]?.let {
            parameter.stringToValue(it)
        } ?: parameter.defaultValue
    }

    fun <T> updateParamValue(parameter: DofusBotParameter<T>, parameterValue: T) {
        this[parameter.key] = parameter.valueToString(parameterValue)
    }

    fun deepCopy(): ParameterValues {
        return ParameterValues().also { it.putAll(this) }
    }

}