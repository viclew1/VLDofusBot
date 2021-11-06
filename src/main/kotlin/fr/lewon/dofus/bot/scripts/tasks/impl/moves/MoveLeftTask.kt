package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.fight.FightBoard

class MoveLeftTask : MoveTask(Direction.LEFT) {

    override fun getOverrideX(): Float {
        return -0.004379562f
    }

    override fun getOverrideY(): Float? {
        return null
    }

    override fun isCellOk(cellData: CellData): Boolean {
        val mapChangeData = cellData.mapChangeData
        return mapChangeData and 8 != 0 || mapChangeData and 16 != 0
            || cellData.cellId >= FightBoard.MAP_WIDTH * 2 && mapChangeData and 32 != 0
    }

}