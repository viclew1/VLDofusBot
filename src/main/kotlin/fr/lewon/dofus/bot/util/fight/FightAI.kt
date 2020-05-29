package fr.lewon.dofus.bot.util.fight


class FightAI(
    private val playerMovePoints: Int,
    private val enemyMovePoints: Int,
    private val fightBoard: FightBoard,
    private val minDist: Int,
    private val maxDist: Int,
    private val initialDepth: Int
) {

    private fun playPlayerMove(state: FightState, targetCell: FightCell) {
        state.fb.playerPos = targetCell
        val dist = state.fb.getDist(targetCell, state.fb.enemyPos) ?: error("Invalid board")
        val los = state.fb.lineOfSight(targetCell, state.fb.enemyPos)
        when {
            dist in minDist..maxDist -> {
                state.attacksDone += if (los) 1000 else 40
            }
            dist < minDist -> {
                state.attacksDone += if (los) 200 else 60
            }
        }
    }

    private fun playEnemyMove(state: FightState, targetCell: FightCell) {
        state.fb.enemyPos = targetCell
    }

    fun selectBestDest(): FightCell {
        var chosenCell = fightBoard.playerPos
        var best = Int.MIN_VALUE
        for (cell in fightBoard.accessibleCells) {
            val state = FightState(0, fightBoard.clone())
            playPlayerMove(state, cell)
            val value = minValue(state, initialDepth)
            if (value > best) {
                chosenCell = cell
                best = value
            }
        }
        return chosenCell
    }


    private fun maxValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.cellsAtRange(playerMovePoints, state.fb.playerPos)

        var v = Int.MIN_VALUE
        for (cell in accessibleCells) {
            if (state.fb.enemyPos != cell) {
                val newState = state.clone()
                playPlayerMove(newState, cell)
                v = maxOf(v, minValue(newState, depth - 1))
            }
        }
        return v
    }

    private fun minValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.cellsAtRange(enemyMovePoints, state.fb.enemyPos)

        var v = Int.MAX_VALUE
        for (cell in accessibleCells) {
            if (state.fb.playerPos != cell) {
                val newState = state.clone()
                playEnemyMove(newState, cell)
                v = minOf(v, maxValue(newState, depth - 1))
            }
        }
        return v
    }

    private fun evaluateState(state: FightState): Int {
        val dist = state.fb.getDist(state.fb.playerPos, state.fb.enemyPos) ?: error("Invalid board")
        val los = state.fb.lineOfSight(state.fb.playerPos, state.fb.enemyPos)
        var score = state.attacksDone - 5 * dist
        if (los) score += 100
        return score
    }

    private class FightState(var attacksDone: Int, val fb: FightBoard) {
        fun clone(): FightState {
            return FightState(attacksDone, fb.clone())
        }
    }

}