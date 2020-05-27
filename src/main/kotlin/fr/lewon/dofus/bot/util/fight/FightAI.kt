package fr.lewon.dofus.bot.util.fight


class FightAI(
    private val playerMovePoints: Int,
    private val enemyMovePoints: Int,
    private val fightBoard: FightBoard,
    private val minDist: Int,
    private val maxDist: Int,
    private val initialDepth: Int
) {

    private var lastTurnMpUsed: Int = playerMovePoints

    fun selectBestDest(): FightCell {
        var chosenCell = fightBoard.yourPos
        var best: Int? = null
        for (cell in fightBoard.accessibleCells) {
            val tmp: FightBoard = fightBoard.clone()
            tmp.yourPos = cell
            val value = minValue(tmp, Int.MIN_VALUE, Int.MAX_VALUE, initialDepth)
            if (best == null || value > best) {
                chosenCell = cell
                best = value
            }
        }
        return chosenCell
    }


    private fun maxValue(fb: FightBoard, alpha: Int, beta: Int, depth: Int): Int {
        var newAlpha = alpha
        val score = evaluateBoard(fb)
        if (depth <= 0) {
            return score
        }

        val mpAvailable = minOf(lastTurnMpUsed, playerMovePoints)
        val accessibleCells = fb.cellsAtRange(mpAvailable, fb.yourPos)

        var v = Int.MIN_VALUE
        for (cell in accessibleCells) {
            val newFB = fb.clone()
            if (newFB.enemyPos != cell) {
                newFB.yourPos = cell
                v = maxOf(v, score + minValue(newFB, newAlpha, beta, depth - 1))
                if (v >= beta) return v
                newAlpha = maxOf(newAlpha, v)
            }
        }
        return v
    }

    private fun minValue(fb: FightBoard, alpha: Int, beta: Int, depth: Int): Int {
        var newBeta = beta
        val score = evaluateBoard(fb)
        if (depth <= 0) {
            return score
        }

        val accessibleCells = fb.cellsAtRange(enemyMovePoints, fb.enemyPos)

        var v = Int.MAX_VALUE
        for (cell in accessibleCells) {
            val newFB = fb.clone()
            if (newFB.enemyPos != cell) {
                newFB.enemyPos = cell
                v = minOf(v, score + maxValue(newFB, alpha, newBeta, depth - 1))
                if (v <= alpha) return v
                newBeta = minOf(newBeta, v)
            }
        }
        return v
    }

    private fun evaluateBoard(fb: FightBoard): Int {
        val dist = fb.getDist(fb.yourPos, fb.enemyPos) ?: error("Invalid board")
        val los = fb.lineOfSight(fb.yourPos, fb.enemyPos)
        val score = when {
            dist in minDist..maxDist -> {
                Pair(1000, 40)
            }
            dist < minDist -> {
                Pair(200, 60)
            }
            else -> Pair(0, 0)
        }
        return (if (los) score.first else score.second) - dist
    }

}