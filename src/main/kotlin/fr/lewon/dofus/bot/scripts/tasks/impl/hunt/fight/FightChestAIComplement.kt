package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.fight

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.DangerMap
import fr.lewon.dofus.bot.game.fight.ai.complements.AIComplement

class FightChestAIComplement : AIComplement() {

    override fun canAttack(playerFighter: Fighter): Boolean {
        return true
    }

    override fun canMove(playerFighter: Fighter): Boolean {
        return false
    }

    override fun mustUseAllMP(playerFighter: Fighter): Boolean {
        return false
    }

    override fun getIdealDistance(playerFighter: Fighter): Int {
        return Int.MAX_VALUE
    }

    override fun shouldAvoidUsingMp(): Boolean {
        return true
    }

    override fun buildDangerMap(dofusBoard: DofusBoard, fightBoard: FightBoard): DangerMap {
        return DangerMap()
    }
}