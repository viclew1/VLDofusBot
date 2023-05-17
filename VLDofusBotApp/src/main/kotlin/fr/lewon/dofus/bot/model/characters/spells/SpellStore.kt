package fr.lewon.dofus.bot.model.characters.spells

class SpellStore : HashMap<String, CharacterSpells>() {

    fun getCharacterSpells(characterName: String): CharacterSpells {
        return computeIfAbsent(characterName) { CharacterSpells() }
    }

    fun setCharacterSpells(characterName: String, spells: CharacterSpells) {
        this[characterName] = spells
    }

}