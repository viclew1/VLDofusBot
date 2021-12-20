package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.DofusBoard

class MoveBottomTask(linkedZoneCellId: Int? = null) : MoveTask(Direction.BOTTOM, linkedZoneCellId) {

    override fun getDefaultMoveCell(): Int {
        return 545
    }

    override fun getOverrideX(): Float? {
        return null
    }

    override fun getOverrideY(): Float {
        return 0.8740876f
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return cellData.cellId >= DofusBoard.MAP_CELLS_COUNT - DofusBoard.MAP_WIDTH * 2
                && (mapChangeData and 2 != 0 || mapChangeData and 4 != 0)
                && cellData.cellId != 532 && cellData.cellId != 559
    }
}