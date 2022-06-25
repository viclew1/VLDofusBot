package fr.lewon.dofus.bot.model.characters

import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder

class VldbScriptValuesStore : HashMap<String, VldbScriptValues>() {

    fun getValues(scriptBuilder: DofusBotScriptBuilder): VldbScriptValues {
        return getValues(scriptBuilder.name)
    }

    fun getValues(scriptBuilderName: String): VldbScriptValues {
        return this.computeIfAbsent(scriptBuilderName) { VldbScriptValues() }
    }

}