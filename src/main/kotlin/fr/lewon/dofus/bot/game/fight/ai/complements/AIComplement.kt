package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter

abstract class AIComplement {

    abstract fun canAttack(playerFighter: Fighter): Boolean
    abstract fun canMove(playerFighter: Fighter): Boolean
    abstract fun mustUseAllMP(playerFighter: Fighter): Boolean
    abstract fun getIdealDistance(playerFighter: Fighter, spells: List<DofusSpellLevel>, playerRange: Int): Int
    abstract fun shouldAvoidUsingMp(): Boolean
    abstract fun buildDangerByCell(dofusBoard: DofusBoard, fightBoard: FightBoard): Map<Int, Int>

}