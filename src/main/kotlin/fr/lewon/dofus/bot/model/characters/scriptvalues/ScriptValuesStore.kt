package fr.lewon.dofus.bot.model.characters.scriptvalues

import fr.lewon.dofus.bot.model.characters.DofusCharacter

class ScriptValuesStore : HashMap<String, CharacterScriptValues>() {

    fun getScriptValues(character: DofusCharacter): CharacterScriptValues {
        return getScriptValues(character.pseudo)
    }

    fun getScriptValues(characterName: String): CharacterScriptValues {
        return computeIfAbsent(characterName) { CharacterScriptValues() }
    }

}