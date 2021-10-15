package fr.lewon.dofus.bot.game.fight

import fr.lewon.dofus.bot.core.manager.d2p.maps.cell.CellData
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative

class FightCell(
    val row: Int,
    val col: Int,
    val cellId: Int,
    val bounds: RectangleRelative,
    var cellData: CellData = CellData(cellId)
) {

    val neighbors = HashSet<FightCell>()

    fun getCenter(): PointRelative {
        return bounds.getCenter()
    }

    fun isAccessible(): Boolean {
        return cellData.mov && !cellData.nonWalkableDuringFight
    }

    fun isWall(): Boolean {
        return !cellData.los
    }

    fun isHole(): Boolean {
        return !isAccessible() && !isWall()
    }

}