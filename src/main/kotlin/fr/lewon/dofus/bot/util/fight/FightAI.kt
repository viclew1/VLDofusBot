package fr.lewon.dofus.bot.util.fight


class FightAI(
    private val playerMovePoints: Int,
    private val enemyMovePoints: Int,
    private val fightBoard: FightBoard,
    private val minDist: Int,
    private val maxDist: Int,
    private val initialDepth: Int
) {

    fun selectBestDest(): FightCell {
        var chosenCell = fightBoard.playerPos
        var best = Int.MIN_VALUE
        for (cell in fightBoard.accessibleCells) {
            val dToStart = fightBoard.getDist(cell, fightBoard.playerPos) ?: error("Couldn't retrieve dist")
            val remaining = maxOf(0, playerMovePoints - dToStart)
            val availableMp = playerMovePoints - remaining
            val tmp: FightBoard = fightBoard.clone()
            tmp.playerPos = cell
            val value = minValue(tmp, emptyList(), initialDepth)
            if (value > best) {
                chosenCell = cell
                best = value
            }
        }
        return chosenCell
    }


    private fun maxValue(
        fb: FightBoard,
        playerEnemyLocs: List<Pair<FightCell, FightCell>>,
        depth: Int
    ): Int {

        val newPlayerEnemyLocs = ArrayList(playerEnemyLocs)
        newPlayerEnemyLocs.add(Pair(fb.playerPos, fb.enemyPos))
        if (depth <= 0) {
            return evaluateBoard(fb, newPlayerEnemyLocs)
        }

        val accessibleCells = fb.cellsAtRange(playerMovePoints, fb.playerPos)

        var v = Int.MIN_VALUE
        for (cell in accessibleCells) {
            if (fb.enemyPos != cell) {
                val newFB = fb.clone()
                newFB.playerPos = cell
                v = maxOf(v, minValue(newFB, newPlayerEnemyLocs, depth - 1))
            }
        }
        return v
    }

    private fun minValue(
        fb: FightBoard,
        playerEnemyLocs: List<Pair<FightCell, FightCell>>,
        depth: Int
    ): Int {

        val newPlayerEnemyLocs = ArrayList(playerEnemyLocs)
        newPlayerEnemyLocs.add(Pair(fb.playerPos, fb.enemyPos))
        if (depth <= 0) {
            return evaluateBoard(fb, newPlayerEnemyLocs)
        }

        val accessibleCells = fb.cellsAtRange(enemyMovePoints, fb.enemyPos)

        var v = Int.MAX_VALUE
        for (cell in accessibleCells) {
            if (fb.playerPos != cell) {
                val newFB = fb.clone()
                newFB.enemyPos = cell
                v = minOf(v, maxValue(newFB, playerEnemyLocs, depth - 1))
            }
        }
        return v
    }

    private fun evaluateBoard(fb: FightBoard, playerEnemyLocs: List<Pair<FightCell, FightCell>>): Int {
        var score = 0
        val size = playerEnemyLocs.size
        for (i in playerEnemyLocs.indices) {
            val tmp = fb.clone()
            tmp.playerPos = playerEnemyLocs[i].first
            tmp.enemyPos = playerEnemyLocs[i].second
            score += (size - i) * evaluateBoard(tmp)
        }
        return score
    }

    private fun evaluateBoard(fb: FightBoard): Int {
        val dist = fb.getDist(fb.playerPos, fb.enemyPos) ?: error("Invalid board")
        val los = fb.lineOfSight(fb.playerPos, fb.enemyPos)
        val score = when {
            dist in minDist..maxDist -> {
                Pair(1000 - dist, 40 - dist)
            }
            dist < minDist -> {
                Pair(200 - dist, 60 - dist)
            }
            else -> Pair(-dist, -dist)
        }
        return if (los) score.first else score.second
    }

}