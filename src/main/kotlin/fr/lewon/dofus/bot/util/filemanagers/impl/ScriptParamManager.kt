package fr.lewon.dofus.bot.util.filemanagers.impl

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.scriptvalues.CharacterScriptValues
import fr.lewon.dofus.bot.model.characters.scriptvalues.ScriptValuesStore
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.util.filemanagers.FileManager
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager

object ScriptParamManager : FileManager<ScriptValuesStore>("script_values", ScriptValuesStore()) {

    override fun getNeededManagers(): List<ToInitManager> {
        return listOf(CharacterManager)
    }

    override fun getStoreClass(): Class<ScriptValuesStore> {
        return ScriptValuesStore::class.java
    }

    fun getCharacterScriptValues(character: DofusCharacter): CharacterScriptValues {
        return store.getScriptValues(character).deepCopy()
    }

    fun updateParamValue(
        character: DofusCharacter,
        scriptBuilder: DofusBotScriptBuilder,
        parameter: DofusBotParameter,
        value: String
    ) {
        store.getScriptValues(character).getValues(scriptBuilder).updateParamValue(parameter, value)
        saveStore()
    }

    fun removeScriptParams(character: DofusCharacter) {
        store.remove(character.pseudo)
        saveStore()
    }

}