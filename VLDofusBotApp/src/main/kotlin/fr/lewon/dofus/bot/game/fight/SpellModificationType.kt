package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel

enum class SpellModificationType(
    private val typeInt: Int,
    val getUpdatedSpell: (spell: DofusSpellLevel, value: Int) -> DofusSpellLevel
) {
    INVALID_MODIFICATION(0, { spell, _ ->
        spell
    }),
    RANGEABLE(1, { spell, value ->
        spell.copy(rangeCanBeBoosted = value > 0)
    }),
    DAMAGE(2, { spell, value ->
        spell // Not implemented yet
    }),
    BASE_DAMAGE(3, { spell, value ->
        spell // Not implemented yet
    }),
    HEAL_BONUS(4, { spell, value ->
        spell // Not implemented yet
    }),
    AP_COST(5, { spell, value ->
        spell.copy(apCost = spell.apCost - value)
    }),
    CAST_INTERVAL(6, { spell, value ->
        spell.copy(minCastInterval = spell.minCastInterval - value)
    }),
    CAST_INTERVAL_SET(7, { spell, value ->
        spell // Not implemented yet
    }),
    CRITICAL_HIT_BONUS(8, { spell, value ->
        spell.copy(criticalHitProbability = spell.criticalHitProbability + value)
    }),
    CAST_LINE(9, { spell, value ->
        spell.copy(castInLine = value == 0)
    }),
    LOS(10, { spell, value ->
        spell.copy(castTestLos = value == 0)
    }),
    MAX_CAST_PER_TURN(11, { spell, value ->
        spell.copy(maxCastPerTurn = spell.maxCastPerTurn + value)
    }),
    MAX_CAST_PER_TARGET(12, { spell, value ->
        spell.copy(maxCastPerTarget = spell.maxCastPerTarget + value)
    }),
    RANGE_MAX(13, { spell, value ->
        spell.copy(maxRange = spell.maxRange + value)
    }),
    RANGE_MIN(14, { spell, value ->
        spell.copy(minRange = spell.minRange + value)
    });

    companion object {
        fun fromTypeInt(typeInt: Int) = SpellModificationType.values().firstOrNull { it.typeInt == typeInt }
    }
}