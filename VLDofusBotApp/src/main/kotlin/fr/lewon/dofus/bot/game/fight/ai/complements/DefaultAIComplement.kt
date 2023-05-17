package fr.lewon.dofus.bot.game.fight.ai.complements

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard
import fr.lewon.dofus.bot.game.fight.Fighter
import fr.lewon.dofus.bot.game.fight.ai.DangerMap

class DefaultAIComplement(
    private val canAttack: Boolean = true,
    private val canMove: Boolean = true,
    private val shouldUseAllMP: Boolean = false,
    private val idealDist: Int = 5
) : AIComplement() {

    override fun canAttack(playerFighter: Fighter): Boolean {
        return canAttack
    }

    override fun canMove(playerFighter: Fighter): Boolean {
        return canMove
    }

    override fun mustUseAllMP(playerFighter: Fighter): Boolean {
        return shouldUseAllMP
    }

    override fun getIdealDistance(playerFighter: Fighter): Int {
        return idealDist
    }

    override fun shouldAvoidUsingMp(): Boolean {
        return true
    }

    override fun buildDangerMap(dofusBoard: DofusBoard, fightBoard: FightBoard): DangerMap {
        val dangerMap = DangerMap()
        val playerFighter = fightBoard.getPlayerFighter()
            ?: error("Player fighter not found")
        fightBoard.getAlliedFighters().forEach { fightBoard.killFighter(it.id) }
        for (enemy in fightBoard.getEnemyFighters()) {
            dangerMap.recalculateDanger(dofusBoard, fightBoard.deepCopy(), playerFighter, enemy)
        }
        return dangerMap
    }
}