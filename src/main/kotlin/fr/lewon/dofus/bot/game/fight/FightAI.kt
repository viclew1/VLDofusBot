package fr.lewon.dofus.bot.game.fight


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
        val dist = state.fb.getDist(state.fb.playerPos, state.fb.enemyPos) ?: error("Invalid board")
        val los = state.fb.lineOfSight(state.fb.playerPos, state.fb.enemyPos)
        if (los && dist in minDist..maxDist) {
            state.attacksDone += 2
        } else if (dist < minDist) {
            state.attacksDone += 1
        }
    }

    private fun playEnemyMove(state: FightState, targetCell: FightCell) {
        state.fb.enemyPos = targetCell
    }

    fun selectBestDest(): FightCell {
        var chosenCell = fightBoard.playerPos
        var best = evaluateMove(fightBoard.playerPos)

        for (cell in fightBoard.accessibleCells) {
            evaluateMove(cell).takeIf { it > best }?.let {
                chosenCell = cell
                best = it
            }
        }
        println("Best dest score : $best")
        return chosenCell
    }

    private fun evaluateMove(cell: FightCell): Int {
        val state = FightState(0, fightBoard.clone())
        playPlayerMove(state, cell)
        return minValue(state, initialDepth)
    }

    private fun maxValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.moveCells(playerMovePoints, state.fb.playerPos)

        var v = Int.MIN_VALUE
        for (cell in accessibleCells) {
            val newState = state.clone()
            playPlayerMove(newState, cell)
            v = maxOf(v, minValue(newState, depth - 1))
        }
        return v
    }

    private fun minValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.moveCells(enemyMovePoints, state.fb.enemyPos)

        var v = Int.MAX_VALUE
        for (cell in accessibleCells) {
            val newState = state.clone()
            playEnemyMove(newState, cell)
            v = minOf(v, maxValue(newState, depth - 1))
        }
        return v
    }

    private fun evaluateState(state: FightState): Int {
        val dist = state.fb.getPathLength(state.fb.playerPos, state.fb.enemyPos) ?: error("Invalid board")
        return state.attacksDone * 2000 - 5 * dist
    }

    private class FightState(var attacksDone: Int, val fb: FightBoard) {
        fun clone(): FightState {
            return FightState(attacksDone, fb.clone())
        }
    }

}