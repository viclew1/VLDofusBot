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
    CRITICAL_HIT_BONUS(7, { spell, value ->
        spell.copy(criticalHitProbability = spell.criticalHitProbability + value)
    }),
    CAST_LINE(8, { spell, value ->
        spell.copy(castInLine = value == 0)
    }),
    LOS(9, { spell, value ->
        spell.copy(castTestLos = value == 0)
    }),
    MAX_CAST_PER_TURN(10, { spell, value ->
        spell.copy(maxCastPerTurn = spell.maxCastPerTurn + value)
    }),
    MAX_CAST_PER_TARGET(11, { spell, value ->
        spell.copy(maxCastPerTarget = spell.maxCastPerTarget + value)
    }),
    RANGE_MAX(12, { spell, value ->
        spell.copy(maxRange = spell.maxRange + value)
    }),
    RANGE_MIN(13, { spell, value ->
        spell.copy(minRange = spell.minRange + value)
    }),
    OCCUPIED_CELL(14, { spell, value ->
        spell // Not implemented yet
    }),
    FREE_CELL(15, { spell, value ->
        spell // Not implemented yet
    }),
    VISIBLE_TARGET(16, { spell, value ->
        spell // Not implemented yet
    }),
    PORTAL_FREE_CELL(17, { spell, value ->
        spell // Not implemented yet
    }),
    PORTAL_PROJECTION(18, { spell, value ->
        spell // Not implemented yet
    });

    companion object {
        fun fromTypeInt(typeInt: Int) = SpellModificationType.values().firstOrNull { it.typeInt == typeInt }
    }
}