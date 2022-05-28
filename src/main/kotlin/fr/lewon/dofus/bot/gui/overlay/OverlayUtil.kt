package fr.lewon.dofus.bot.gui.overlay

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.gui.overlay.line.OverlayInfoLine
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.image.BufferedImage

object OverlayUtil {

    fun buildPolygon(gameInfo: GameInfo, pointRelatives: List<PointRelative>): Polygon {
        val origin = PointAbsolute(gameInfo.gameBounds.x, gameInfo.gameBounds.y)
        val absolutePoints = pointRelatives.map { ConverterUtil.toPointAbsolute(gameInfo, it).getDifference(origin) }
        val xPoints = absolutePoints.map { it.x }.toIntArray()
        val yPoints = absolutePoints.map { it.y }.toIntArray()
        return Polygon(xPoints, yPoints, absolutePoints.size)
    }

    fun drawHoverInfoWindow(
        g: Graphics,
        overlay: AbstractOverlay,
        hoveredCell: DofusCell,
        toDrawLines: List<OverlayInfoLine>
    ) {
        val totalHeight = toDrawLines.sumOf { it.getHeight(g) } + 10
        val width = toDrawLines.maxOfOrNull { it.getWidth(g) } ?: return
        val infoWindow = BufferedImage(width + 30, totalHeight, BufferedImage.TYPE_INT_RGB)
        val g2 = infoWindow.graphics as Graphics2D
        g2.color = Color.DARK_GRAY
        g2.fillRect(0, 0, infoWindow.width, infoWindow.height)
        var currentY = 0
        for (line in toDrawLines) {
            g2.color = Color.WHITE
            line.draw(g2, 0, currentY)
            currentY += line.getHeight(g)
        }

        val origin = PointAbsolute(overlay.gameInfo.gameBounds.x, overlay.gameInfo.gameBounds.y)
        val drawOrigin = ConverterUtil.toPointAbsolute(overlay.gameInfo, hoveredCell.getCenter())
            .getDifference(origin)
        val bottomRightPointAbsolute = ConverterUtil.toPointAbsolute(overlay.gameInfo, hoveredCell.getCenter())
            .getSum(PointAbsolute(infoWindow.width, infoWindow.height))
        val bottomRightPointRelative = ConverterUtil.toPointRelative(overlay.gameInfo, bottomRightPointAbsolute)
        var x = drawOrigin.x
        if (bottomRightPointRelative.x > 1.0f) {
            x -= infoWindow.width
        }
        var y = drawOrigin.y
        if (bottomRightPointRelative.y > 1.0f) {
            y -= infoWindow.height
        }
        g.drawImage(infoWindow, x, y, null)
    }

}