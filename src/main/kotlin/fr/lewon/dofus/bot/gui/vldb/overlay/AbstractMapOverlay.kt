package fr.lewon.dofus.bot.gui.vldb.overlay

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Polygon
import java.awt.Rectangle

abstract class AbstractMapOverlay : AbstractOverlay() {

    var hitBoxByCell: Map<DofusCell, Polygon> = HashMap()

    abstract override fun buildContentPane(): AbstractMapOverlayPanel

    override fun updateOverlay(gameInfo: GameInfo) {
        super.updateOverlay(gameInfo)
        hitBoxByCell = gameInfo.dofusBoard.cells.associateWith { buildCellHitBox(it) }
    }

    private fun buildCellHitBox(cell: DofusCell): Polygon {
        val center = cell.getCenter()
        return OverlayUtil.buildPolygon(
            gameInfo,
            listOf(
                PointRelative(center.x, center.y - cell.bounds.height),
                PointRelative(center.x + cell.bounds.width, center.y),
                PointRelative(center.x, center.y + cell.bounds.height),
                PointRelative(center.x - cell.bounds.width, center.y)
            )
        )
    }

    override fun buildOverlayBounds(): Rectangle {
        return gameInfo.gameBounds
    }
}