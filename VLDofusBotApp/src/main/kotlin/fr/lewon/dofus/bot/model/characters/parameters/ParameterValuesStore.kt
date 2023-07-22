package fr.lewon.dofus.bot.model.characters.parameters

import fr.lewon.dofus.bot.model.characters.DofusCharacter

class ParameterValuesStore : HashMap<String, CharacterParameterValues>() {

    fun getCharacterParameterValues(character: DofusCharacter): CharacterParameterValues {
        return getCharacterParameterValues(character.name)
    }

    fun getCharacterParameterValues(characterName: String): CharacterParameterValues {
        return computeIfAbsent(characterName) { CharacterParameterValues() }
    }

}