package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.DangerMap

class TreasureChestAIComplement : AIComplement() {

    override fun canAttack(playerFighter: Fighter): Boolean {
        return playerFighter.maxHp / 6 < playerFighter.getCurrentHp()
    }

    override fun canMove(playerFighter: Fighter): Boolean {
        return true
    }

    override fun mustUseAllMP(playerFighter: Fighter): Boolean {
        return false
    }

    override fun getIdealDistance(playerFighter: Fighter): Int {
        return 0
    }

    override fun shouldAvoidUsingMp(): Boolean {
        return false
    }

    override fun buildDangerMap(dofusBoard: DofusBoard, fightBoard: FightBoard): DangerMap {
        return DangerMap()
    }

}