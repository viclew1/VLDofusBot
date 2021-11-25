package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.DofusBoard

class MoveLeftTask(linkedZoneCellId: Int? = null) : MoveTask(Direction.LEFT, linkedZoneCellId) {

    override fun getDefaultMoveCell(): Int {
        return 0
    }

    override fun getOverrideX(): Float {
        return -0.004379562f
    }

    override fun getOverrideY(): Float? {
        return null
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return mapChangeData and 16 != 0
                || cellData.cellId >= DofusBoard.MAP_WIDTH * 2 && mapChangeData and 32 != 0
                || cellData.cellId < DofusBoard.MAP_CELLS_COUNT - DofusBoard.MAP_WIDTH * 2 && mapChangeData and 8 != 0
    }

}