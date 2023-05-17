package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.DangerMap

abstract class AIComplement {

    abstract fun canAttack(playerFighter: Fighter): Boolean
    abstract fun canMove(playerFighter: Fighter): Boolean
    abstract fun mustUseAllMP(playerFighter: Fighter): Boolean
    abstract fun getIdealDistance(playerFighter: Fighter): Int
    abstract fun shouldAvoidUsingMp(): Boolean
    abstract fun buildDangerMap(dofusBoard: DofusBoard, fightBoard: FightBoard): DangerMap

}