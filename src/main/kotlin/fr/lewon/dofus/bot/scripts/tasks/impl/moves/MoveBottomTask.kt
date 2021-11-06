package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction

class MoveBottomTask : MoveTask(Direction.BOTTOM) {

    override fun getOverrideX(): Float? {
        return null
    }

    override fun getOverrideY(): Float {
        return 0.8740876f
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return mapChangeData and 2 != 0 || mapChangeData and 4 != 0
    }
}