package fr.lewon.dofus.bot.model.characters.spells

import fr.lewon.dofus.bot.core.model.spell.DofusSpell

data class CharacterSpell(
    var spell: DofusSpell = DofusSpell(),
    var key: String = "",
    var type: SpellType = SpellType.NAMED_SPELL
)