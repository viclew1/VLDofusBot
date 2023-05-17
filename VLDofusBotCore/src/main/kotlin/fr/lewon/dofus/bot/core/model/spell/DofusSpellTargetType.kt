package fr.lewon.dofus.bot.core.model.spell

import fr.lewon.dofus.bot.core.fighter.IDofusFighter
import fr.lewon.dofus.bot.core.fighter.PlayerType

enum class DofusSpellTargetType(
    private val keys: List<Char>,
    private val isTargetValidChecker: (Int?, IDofusFighter, IDofusFighter) -> Boolean
) {
    ALLIES(listOf('a'), { _, caster, target ->
        caster.getFighterTeamId() == target.getFighterTeamId()
    }),
    ENEMIES(listOf('A'), { _, caster, target ->
        caster.getFighterTeamId() != target.getFighterTeamId()
    }),
    CASTER(listOf('c', 'C'), { _, caster, target ->
        caster.getFighterId() == target.getFighterId()
    }),
    SPECIFIC_ALLIED_SUMMON(listOf('F'), { id, _, target ->
        target.getPlayerType() != PlayerType.HUMAN && target.getBreed() == id
    }),
    SWITCHABLE_ENEMIES(listOf('i'), { _, caster, target ->
        caster.getFighterTeamId() != target.getFighterTeamId()
                && target.getPlayerType() != PlayerType.SIDEKICK
                && target.isSummon()
                && !target.isStaticElement()
    }),
    PLAYER_SUMMON(listOf('P', 'p'), { _, caster, target ->
        target.getFighterId() == caster.getFighterId()
                || target.isSummon() && target.getSummonerId() == caster.getFighterId()
                || target.isSummon() && caster.getSummonerId() == target.getSummonerId()
                || caster.isSummon() && caster.getSummonerId() == target.getFighterId()
    }),
    WITHOUT_STATE(listOf('e'), { id, _, target ->
        id != null && !target.hasState(id)
    }),
    WITH_STATE(listOf('E'), { id, _, target ->
        id != null && target.hasState(id)
    }),
    ENEMY_SUMMON(listOf('J'), { _, caster, target ->
        target.getPlayerType() != PlayerType.SIDEKICK
                && target.getFighterTeamId() != caster.getFighterTeamId()
                && target.isSummon()
    }),
    ALLIED_SUMMON(listOf('j'), { _, caster, target ->
        target.getFighterTeamId() == caster.getFighterTeamId()
                && target.getPlayerType() != PlayerType.SIDEKICK
                && target.isSummon()
    }),
    ENEMY_PLAYER(listOf('L'), { _, caster, target ->
        target.getFighterTeamId() != caster.getFighterTeamId()
                && (target.getPlayerType() == PlayerType.HUMAN
                && !target.isSummon()
                || target.getPlayerType() == PlayerType.SIDEKICK)
    }),
    ALLIED_PLAYER(listOf('l'), { _, caster, target ->
        target.getFighterTeamId() == caster.getFighterTeamId()
                && (target.getPlayerType() == PlayerType.HUMAN
                && !target.isSummon()
                || target.getPlayerType() == PlayerType.SIDEKICK)
    }),
    NON_HUMAN_NON_SUMMON_ALLY(listOf('m'), { _, caster, target ->
        target.getFighterTeamId() == caster.getFighterTeamId()
                && target.getPlayerType() != PlayerType.HUMAN
                && !target.isSummon()
                && !target.isStaticElement()
    }),
    NON_HUMAN_NON_SUMMON_ENEMY(listOf('M'), { _, caster, target ->
        target.getFighterTeamId() != caster.getFighterTeamId()
                && target.getPlayerType() != PlayerType.HUMAN
                && !target.isSummon()
                && !target.isStaticElement()
    })
    ;

    companion object {
        fun fromString(targetChar: Char): DofusSpellTargetType? {
            return values().firstOrNull { it.keys.contains(targetChar) }
        }
    }

    fun canHitTarget(id: Int?, caster: IDofusFighter, target: IDofusFighter): Boolean {
        return isTargetValidChecker(id, caster, target)
    }

}
