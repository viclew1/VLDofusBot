package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.DofusBoard

class MoveRightTask(linkedZoneCellId: Int? = null) : MoveTask(Direction.RIGHT, linkedZoneCellId) {

    override fun getDefaultMoveCell(): Int {
        return DofusBoard.MAP_CELLS_COUNT - 1
    }

    override fun getOverrideX(): Float {
        return 0.99708027f
    }

    override fun getOverrideY(): Float? {
        return null
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return mapChangeData and 1 != 0
                || cellData.cellId < DofusBoard.MAP_CELLS_COUNT - DofusBoard.MAP_WIDTH * 2 && mapChangeData and 2 != 0
    }

}