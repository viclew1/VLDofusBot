package fr.lewon.dofus.bot.game.fight.mcts

import fr.lewon.dofus.bot.game.fight.DofusCharacteristics
import fr.lewon.dofus.bot.game.fight.FightBoard


class MCTSState(val fightBoard: FightBoard, var fighterId: Double) {

    fun clone(): MCTSState {
        return MCTSState(fightBoard.deepCopy(), fighterId)
    }

    fun getAllPossibleMoves() {

    }

    fun randomPlay() {
        val fighter = fightBoard.getFighterById(fighterId)
            ?: error("No fighter with id : $fighterId")
        val mp = DofusCharacteristics.MOVEMENT_POINTS.getValue(fighter)
        val availablePositions = this.fightBoard.getMoveCellsWithMpUsed(mp, fighter.cell)
        this.fightBoard.move(this.fighterId, availablePositions.random().first.cellId)
    }

}