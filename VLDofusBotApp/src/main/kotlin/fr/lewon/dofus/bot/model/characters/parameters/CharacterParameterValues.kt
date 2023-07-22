package fr.lewon.dofus.bot.model.characters.parameters

import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder

class CharacterParameterValues : HashMap<String, ParameterValues>() {

    fun getValues(scriptBuilder: DofusBotScriptBuilder): ParameterValues {
        return getValues(scriptBuilder.name)
    }

    fun getValues(scriptBuilderName: String): ParameterValues {
        return this.computeIfAbsent(scriptBuilderName) { ParameterValues() }
    }

    fun deepCopy(): CharacterParameterValues {
        val newEntries = entries.map { it.key to it.value.deepCopy() }
        return CharacterParameterValues().also { it.putAll(newEntries) }
    }

}