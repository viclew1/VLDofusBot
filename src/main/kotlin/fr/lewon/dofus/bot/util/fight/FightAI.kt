package fr.lewon.dofus.bot.util.fight

import kotlin.math.abs

object FightAI {

    fun selectBestDest(fightBoard: FightBoard, objectiveDist: Int, maxDist: Int): FightCell? {
        var bestDest: FightCell? = null
        var bestScore: Int = -100000
        for (moveCell in fightBoard.accessibleCells) {
            evaluateBoard(fightBoard, moveCell, objectiveDist, maxDist)
                .takeIf { it > bestScore }
                ?.let {
                    bestDest = moveCell
                    bestScore = it
                }
        }
        return bestDest
    }

    private fun evaluateBoard(fightBoard: FightBoard, yourPos: FightCell, objectiveDist: Int, maxDist: Int): Int {
        val dist = fightBoard.getDist(yourPos, fightBoard.enemyPos) ?: error("Invalid board")
        val los = fightBoard.lineOfSight(yourPos, fightBoard.enemyPos)
        if (dist == objectiveDist) {
            return if (los) 1000 else 50
        }
        val dToObjective = abs(dist - objectiveDist)
        if (dist < objectiveDist) {
            return (if (los) 100 else 20) - dToObjective
        }
        if (dist <= maxDist) {
            return (if (los) 200 else 40) - dToObjective
        }
        return -dToObjective
    }

}