package fr.lewon.dofus.bot.model.characters

class DTBScriptValues : HashMap<String, HashMap<String, String>>() {

    fun getParamValue(scriptName: String, parameterKey: String): String? {
        val scriptParameters = computeIfAbsent(scriptName) { HashMap() }
        return scriptParameters[parameterKey]
    }

    fun updateParamValue(scriptName: String, parameterKey: String, parameterValue: String) {
        val scriptParameters = computeIfAbsent(scriptName) { HashMap() }
        scriptParameters[parameterKey] = parameterValue
    }

}