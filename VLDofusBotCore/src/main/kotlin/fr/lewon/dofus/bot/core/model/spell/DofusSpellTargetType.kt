package fr.lewon.dofus.bot.core.model.spell

import fr.lewon.dofus.bot.core.fighter.IDofusFighter
import fr.lewon.dofus.bot.core.fighter.PlayerType

enum class DofusSpellTargetType(
    val key: Char,
    val canHitTarget: (id: Int?, caster: IDofusFighter, target: IDofusFighter) -> Boolean
) {
    ALLIES('a', { _, caster, target ->
        caster.getFighterTeamId() == target.getFighterTeamId()
    }),
    ENEMIES('A', { _, caster, target ->
        caster.getFighterTeamId() != target.getFighterTeamId()
    }),
    CASTER_1('c', { _, caster, target ->
        caster.getFighterId() == target.getFighterId()
    }),
    CASTER_2('C', { _, caster, target ->
        caster.getFighterId() == target.getFighterId()
    }),
    SPECIFIC_ALLIED_SUMMON('F', { id, _, target ->
        target.getPlayerType() != PlayerType.HUMAN && target.getBreed() == id
    }),
    SWITCHABLE_ENEMIES('i', { _, caster, target ->
        caster.getFighterTeamId() != target.getFighterTeamId()
                && target.getPlayerType() != PlayerType.SIDEKICK
                && target.isSummon()
                && !target.isStaticElement()
    }),
    PLAYER_SUMMON_1('P', { _, caster, target ->
        target.getFighterId() == caster.getFighterId()
                || target.isSummon() && target.getSummonerId() == caster.getFighterId()
                || target.isSummon() && caster.getSummonerId() == target.getSummonerId()
                || caster.isSummon() && caster.getSummonerId() == target.getFighterId()
    }),
    PLAYER_SUMMON_2('p', { _, caster, target ->
        target.getFighterId() == caster.getFighterId()
                || target.isSummon() && target.getSummonerId() == caster.getFighterId()
                || target.isSummon() && caster.getSummonerId() == target.getSummonerId()
                || caster.isSummon() && caster.getSummonerId() == target.getFighterId()
    }),
    TARGET_WITHOUT_STATE('e', { id, _, target ->
        id != null && !target.hasState(id)
    }),
    TARGET_WITH_STATE('E', { id, _, target ->
        id != null && target.hasState(id)
    }),
    ENEMY_SUMMON('J', { _, caster, target ->
        target.getPlayerType() != PlayerType.SIDEKICK
                && target.getFighterTeamId() != caster.getFighterTeamId()
                && target.isSummon()
    }),
    ALLIED_SUMMON('j', { _, caster, target ->
        target.getFighterTeamId() == caster.getFighterTeamId()
                && target.getPlayerType() != PlayerType.SIDEKICK
                && target.isSummon()
    }),
    ENEMY_PLAYER('L', { _, caster, target ->
        target.getFighterTeamId() != caster.getFighterTeamId()
                && (target.getPlayerType() == PlayerType.HUMAN
                && !target.isSummon()
                || target.getPlayerType() == PlayerType.SIDEKICK)
    }),
    ALLIED_PLAYER('l', { _, caster, target ->
        target.getFighterTeamId() == caster.getFighterTeamId()
                && (target.getPlayerType() == PlayerType.HUMAN
                && !target.isSummon()
                || target.getPlayerType() == PlayerType.SIDEKICK)
    }),
    NON_HUMAN_NON_SUMMON_ALLY('m', { _, caster, target ->
        target.getFighterTeamId() == caster.getFighterTeamId()
                && target.getPlayerType() != PlayerType.HUMAN
                && !target.isSummon()
                && !target.isStaticElement()
    }),
    NON_HUMAN_NON_SUMMON_ENEMY('M', { _, caster, target ->
        target.getFighterTeamId() != caster.getFighterTeamId()
                && target.getPlayerType() != PlayerType.HUMAN
                && !target.isSummon()
                && !target.isStaticElement()
    })
    ;

    companion object {
        fun fromKey(key: Char): DofusSpellTargetType? {
            return values().firstOrNull { key == it.key }
        }
    }

}
