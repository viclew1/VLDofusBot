package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.fight.FightBoard

class AlignmentCalculator(dofusBoard: DofusBoard, fightBoard: FightBoard, fromCellId: Int, toCellId: Int) {
    val dist: Int by lazy {
        dofusBoard.getDist(fromCellId, toCellId)
    }
    val los: Boolean by lazy {
        fightBoard.lineOfSight(fromCellId, toCellId)
    }
    val onSameLine: Boolean by lazy {
        dofusBoard.isOnSameLine(fromCellId, toCellId)
    }
    val onSameDiagonal: Boolean by lazy {
        dofusBoard.isOnSameDiagonal(fromCellId, toCellId)
    }
}