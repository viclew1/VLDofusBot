package fr.lewon.dofus.bot.model.characters.scriptvalues

import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder

class CharacterScriptValues : HashMap<String, ScriptValues>() {

    fun getValues(scriptBuilder: DofusBotScriptBuilder): ScriptValues {
        return getValues(scriptBuilder.name)
    }

    fun getValues(scriptBuilderName: String): ScriptValues {
        return this.computeIfAbsent(scriptBuilderName) { ScriptValues() }
    }

    fun deepCopy(): CharacterScriptValues {
        val newEntries = entries.map { it.key to it.value.deepCopy() }
        return CharacterScriptValues().also { it.putAll(newEntries) }
    }

}