package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter

class VldbScriptValues : HashMap<String, String>() {

    fun getParamValue(parameter: DofusBotParameter): String {
        return computeIfAbsent(parameter.key) { parameter.defaultValue }
    }

    fun updateParamValue(parameter: DofusBotParameter, parameterValue: String) {
        this[parameter.key] = parameterValue
    }

    fun deepCopy(): VldbScriptValues {
        return VldbScriptValues().also { it.putAll(this) }
    }

}