package fr.lewon.dofus.bot.model.characters.spells

data class CharacterSpell(
    var spellId: Int? = null,
    var key: Char = 'x',
    var ctrlModifier: Boolean = false
)