package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.DofusBoard

class MoveTopTask(linkedZoneCellId: Int? = null) : MoveTask(Direction.TOP, linkedZoneCellId) {

    override fun getDefaultMoveCell(): Int {
        return 0
    }

    override fun getOverrideX(): Float? {
        return null
    }

    override fun getOverrideY(): Float {
        return 0.0054744524f
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return cellData.cellId < DofusBoard.MAP_WIDTH * 2 &&
                (mapChangeData and 32 != 0 || mapChangeData and 64 != 0 || mapChangeData and 128 != 0)
    }
}