package fr.lewon.dofus.bot.game.fight.operations

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel

data class FightOperation(
    val type: FightOperationType,
    val targetCellId: Int? = null,
    val spell: DofusSpellLevel? = null,
    val dist: Int? = null
)