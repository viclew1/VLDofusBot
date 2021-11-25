package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination


class FightAI(
    private val playerMovePoints: Int,
    private val enemyMovePoints: Int,
    private val dofusBoard: DofusBoard,
    private val fightBoard: FightBoard,
    private val losSpellCombination: SpellCombination,
    private val nonLosSpellCombination: SpellCombination,
    private val contactSpellCombination: SpellCombination,
    private val initialDepth: Int
) {

    fun selectBestTpDest(minRange: Int, maxRange: Int): DofusCell {
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val cellsAtRange = dofusBoard.cellsAtRange(minRange, maxRange, playerPosition)
            .filter { it.isAccessible() && !fightBoard.isFighterHere(it) }
        return selectBestCell(playerPosition, cellsAtRange).first
    }

    fun selectBestMoveDest(potentialMpBuff: Int): Pair<DofusCell, Boolean> {
        val playerPosition = fightBoard.getPlayerFighter()?.cell ?: error("Player not found")
        val accessibleCells = fightBoard.getMoveCells(playerMovePoints, playerPosition)
        val accessibleCellsWithBuff = fightBoard.getMoveCells(potentialMpBuff, accessibleCells).toMutableList()
            .filter { !accessibleCells.contains(it) }

        return selectBestCell(playerPosition, accessibleCells, accessibleCellsWithBuff)
    }

    private fun selectBestCell(
        playerPosition: DofusCell,
        accessibleCells: List<DofusCell>,
        accessibleCellsWithBuff: List<DofusCell> = emptyList()
    ): Pair<DofusCell, Boolean> {
        var chosenCell = playerPosition
        var best = evaluateMove(playerPosition, playerPosition)
        var mpBuffUsed = false

        for (cell in accessibleCells) {
            evaluateMove(playerPosition, cell).takeIf { it > best }?.let {
                chosenCell = cell
                best = it
            }
        }
        for (cell in accessibleCellsWithBuff) {
            evaluateMove(playerPosition, cell).takeIf { it - 500 > best }?.let {
                chosenCell = cell
                best = it - 500
                mpBuffUsed = true
            }
        }
        return chosenCell to mpBuffUsed
    }

    private fun evaluateMove(initialPlayerPosition: DofusCell, newPlayerPosition: DofusCell): Int {
        val state = FightState(0, fightBoard.clone(), newPlayerPosition)
        playPlayerMove(state, initialPlayerPosition, newPlayerPosition)
        return minValue(state, initialDepth)
    }

    private fun playPlayerMove(state: FightState, initialPlayerPosition: DofusCell, newPlayerPosition: DofusCell) {
        state.fb.move(initialPlayerPosition.cellId, newPlayerPosition.cellId, false)
        state.playerPosition = newPlayerPosition
        val dist = dofusBoard.getDist(newPlayerPosition, state.fb.closestEnemyPosition) ?: error("Invalid board")
        val los = state.fb.lineOfSight(newPlayerPosition, state.fb.closestEnemyPosition)
        if (losSpellCombination.keys.isNotEmpty() && los && dist in losSpellCombination.minRange..losSpellCombination.maxRange) {
            state.attacksDone += losSpellCombination.aiWeight
        } else if (nonLosSpellCombination.keys.isNotEmpty() && dist in nonLosSpellCombination.minRange..nonLosSpellCombination.maxRange) {
            state.attacksDone += nonLosSpellCombination.aiWeight
        } else if (contactSpellCombination.keys.isNotEmpty() && dist <= 1) {
            state.attacksDone += contactSpellCombination.aiWeight
        }
    }

    private fun playEnemyMove(state: FightState, targetCell: DofusCell) {
        state.fb.closestEnemyPosition = targetCell
    }

    private fun maxValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.getMoveCells(playerMovePoints, state.playerPosition)

        var v = Int.MIN_VALUE
        for (cell in accessibleCells) {
            val newState = state.clone()
            playPlayerMove(newState, state.playerPosition, cell)
            v = maxOf(v, minValue(newState, depth - 1))
        }
        return v
    }

    private fun minValue(state: FightState, depth: Int): Int {
        if (depth <= 0) {
            return evaluateState(state)
        }

        val accessibleCells = state.fb.getMoveCells(enemyMovePoints, state.fb.closestEnemyPosition)

        var v = Int.MAX_VALUE
        for (cell in accessibleCells) {
            val newState = state.clone()
            playEnemyMove(newState, cell)
            v = minOf(v, maxValue(newState, depth - 1))
        }
        return v
    }

    private fun evaluateState(state: FightState): Int {
        val dist = dofusBoard.getPathLength(state.playerPosition, state.fb.closestEnemyPosition) ?: 100000
        return state.attacksDone * 2000 - 5 * dist
    }

    private class FightState(var attacksDone: Int, val fb: FightBoard, var playerPosition: DofusCell) {
        fun clone(): FightState {
            return FightState(attacksDone, fb.clone(), playerPosition)
        }
    }

}