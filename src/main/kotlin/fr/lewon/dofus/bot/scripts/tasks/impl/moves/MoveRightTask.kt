package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction

class MoveRightTask : MoveTask(Direction.RIGHT) {

    override fun getOverrideX(): Float {
        return 0.99708027f
    }

    override fun getOverrideY(): Float? {
        return null
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return mapChangeData and 1 != 0
    }

}