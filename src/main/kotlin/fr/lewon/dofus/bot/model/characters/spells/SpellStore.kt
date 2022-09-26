package fr.lewon.dofus.bot.model.characters.spells

import fr.lewon.dofus.bot.model.characters.DofusCharacter

class SpellStore : HashMap<String, CharacterSpells>() {

    fun getCharacterSpells(character: DofusCharacter): CharacterSpells {
        return getCharacterSpells(character.pseudo)
    }

    private fun getCharacterSpells(characterName: String): CharacterSpells {
        return computeIfAbsent(characterName) { CharacterSpells() }
    }

    fun setCharacterSpells(character: DofusCharacter, spells: CharacterSpells) {
        this[character.pseudo] = spells
    }

}