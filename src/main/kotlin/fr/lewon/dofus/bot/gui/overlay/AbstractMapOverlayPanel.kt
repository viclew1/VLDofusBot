package fr.lewon.dofus.bot.gui.overlay

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import java.awt.Color
import java.awt.Graphics
import java.awt.Point

abstract class AbstractMapOverlayPanel(overlay: AbstractMapOverlay) : AbstractOverlayPanel(overlay) {

    var hoveredCell: DofusCell? = null

    override fun onHover(mouseLocation: Point) {
        hoveredCell = getCellAtLocation(mouseLocation)
            ?.takeIf { it.isAccessible() }
        onCellHover()
    }

    protected abstract fun onCellHover()

    override fun drawOverlay(g: Graphics) {
        val hoveredCell = this.hoveredCell ?: return
        getCellContentInfo(hoveredCell)?.let {
            OverlayUtil.drawHoverInfoWindow(g, overlay, hoveredCell, it)
        }
    }

    protected abstract fun getCellContentInfo(cell: DofusCell): List<String>?

    private fun getCellAtLocation(location: Point): DofusCell? {
        return overlay.hitBoxByCell.entries.firstOrNull { it.value.contains(location) }?.key
    }

    override fun drawBackground(g: Graphics) {
        val holes = overlay.gameInfo.dofusBoard.cells.filter { it.isHole() }
        val walls = overlay.gameInfo.dofusBoard.cells.filter { it.isWall() }.sortedBy { it.cellId }
        val accessibleCells = overlay.gameInfo.dofusBoard.cells.filter { it.isAccessible() }
        accessibleCells.forEach { drawAccessibleCell(g, it) }
        holes.forEach { drawHole(g, it) }
        walls.forEach { drawWall(g, it) }
    }

    private fun drawLine(g: Graphics, from: PointRelative, to: PointRelative) {
        val origin = PointAbsolute(overlay.gameInfo.gameBounds.x, overlay.gameInfo.gameBounds.y)
        val fromAbsolute = ConverterUtil.toPointAbsolute(overlay.gameInfo, from).getDifference(origin)
        val toAbsolute = ConverterUtil.toPointAbsolute(overlay.gameInfo, to).getDifference(origin)
        g.drawLine(fromAbsolute.x, fromAbsolute.y, toAbsolute.x, toAbsolute.y)
    }

    private fun drawHole(g: Graphics, cell: DofusCell) {
        val polygon = overlay.hitBoxByCell[cell] ?: return
        val oldColor = g.color
        g.color = Color.BLACK
        g.fillPolygon(polygon)
        g.color = oldColor
    }

    private fun drawWall(g: Graphics, cell: DofusCell) {
        val center = cell.getCenter()
        val w = cell.bounds.width
        val h = cell.bounds.height
        val topBevelPoint = PointRelative(center.x, center.y - h * 3 / 2f)
        val bottomBevelPoint = PointRelative(center.x, center.y + h / 2f)
        val leftBevelPoint = PointRelative(center.x - w, center.y - h / 2f)
        val rightBevelPoint = PointRelative(center.x + w, center.y - h / 2f)
        val bottomPoint = PointRelative(center.x, center.y + h)
        val leftPoint = PointRelative(center.x - w, center.y)
        val rightPoint = PointRelative(center.x + w, center.y)
        val polygon = OverlayUtil.buildPolygon(
            overlay.gameInfo,
            listOf(leftBevelPoint, topBevelPoint, rightBevelPoint, rightPoint, bottomPoint, leftPoint)
        )
        val oldColor = g.color
        g.color = Color.DARK_GRAY
        g.fillPolygon(polygon)
        g.color = Color.WHITE
        g.drawPolygon(polygon)
        drawLine(g, leftBevelPoint, bottomBevelPoint)
        drawLine(g, rightBevelPoint, bottomBevelPoint)
        drawLine(g, bottomPoint, bottomBevelPoint)
        g.color = oldColor
    }

    private fun drawAccessibleCell(g: Graphics, cell: DofusCell) {
        val color = getCellColor(cell)
        val polygon = overlay.hitBoxByCell[cell] ?: return
        val oldColor = g.color
        g.color = color
        g.fillPolygon(polygon)
        drawAdditionalCellContent(g, cell)
        g.color = Color.WHITE
        g.drawPolygon(polygon)
        g.color = oldColor
    }

    abstract fun getCellColor(cell: DofusCell): Color?

    abstract fun drawAdditionalCellContent(g: Graphics, cell: DofusCell)
}
