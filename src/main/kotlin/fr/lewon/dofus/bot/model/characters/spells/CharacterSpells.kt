package fr.lewon.dofus.bot.model.characters.spells

class CharacterSpells(spells: List<CharacterSpell> = emptyList()) : ArrayList<CharacterSpell>() {

    init {
        addAll(spells.map(CharacterSpell::copy))
    }

    fun deepCopy(): CharacterSpells {
        return CharacterSpells(this)
    }

}