package fr.lewon.dofus.bot.model.characters.spells

import fr.lewon.dofus.bot.core.model.spell.DofusSpell

data class CharacterSpell(
    var spell: DofusSpell? = null,
    var key: Char = 'x',
    var ctrlModifier: Boolean = false
)