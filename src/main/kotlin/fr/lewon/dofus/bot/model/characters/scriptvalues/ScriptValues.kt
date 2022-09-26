package fr.lewon.dofus.bot.model.characters.scriptvalues

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class ScriptValues : HashMap<String, String>() {

    fun getParamValue(parameter: DofusBotParameter): String {
        return computeIfAbsent(parameter.key) { parameter.defaultValue }
    }

    fun updateParamValue(parameter: DofusBotParameter, parameterValue: String) {
        this[parameter.key] = parameterValue
    }

    fun deepCopy(): ScriptValues {
        return ScriptValues().also { it.putAll(this) }
    }

}