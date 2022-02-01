package fr.lewon.dofus.bot.model.characters.spells

import fr.lewon.dofus.bot.core.model.spell.DofusSpell

data class DofusSpellWithKey(var spell: DofusSpell = DofusSpell(), var key: String = "")