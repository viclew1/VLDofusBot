package fr.lewon.dofus.bot.model.characters.sets

import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterSetsManager

class CharacterSetStore : HashMap<String, CharacterSets>() {

    fun getCharacterSets(characterName: String): CharacterSets {
        return computeIfAbsent(characterName) { CharacterSets(characterName = characterName) }
    }

    fun updateCharacterSet(characterName: String, set: CharacterSet) {
        val characterSets = getCharacterSets(characterName)
        val newSets = characterSets.sets.toMutableList()
        val index = newSets.indexOfFirst { it.name == set.name }
        if (index >= 0) {
            newSets[index] = set
        } else {
            newSets.add(set)
            characterSets.selectedSetName = set.name
        }
        characterSets.sets = newSets
    }

    fun selectSet(characterName: String, setName: String) {
        val characterSets = getCharacterSets(characterName)
        if (characterSets.sets.any { it.name == setName }) {
            characterSets.selectedSetName = setName
        }
    }

    fun removeSet(characterName: String, setName: String) {
        val characterSets = getCharacterSets(characterName)
        val newSets = characterSets.sets.toMutableList()
        val index = newSets.indexOfFirst { it.name == setName }
        if (index > 0) {
            newSets.removeAt(index)
        }
        if (characterSets.selectedSetName == setName) {
            characterSets.selectedSetName = CharacterSetsManager.DefaultSetName
        }
        characterSets.sets = newSets
    }

}