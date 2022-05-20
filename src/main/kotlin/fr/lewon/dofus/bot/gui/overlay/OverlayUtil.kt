package fr.lewon.dofus.bot.gui.overlay

import fr.lewon.dofus.bot.game.DofusCell
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
        toDrawLines: List<String>,
        lineHeight: Int = 14
    ) {
        val width = toDrawLines.maxOf { g.fontMetrics.stringWidth(it) } + 10
        val infoWindow = BufferedImage(width, lineHeight * toDrawLines.size + 10, BufferedImage.TYPE_INT_RGB)
        val g2 = infoWindow.graphics as Graphics2D
        g2.color = Color.DARK_GRAY
        g2.fillRect(0, 0, infoWindow.width, infoWindow.height)
        for ((index, line) in toDrawLines.withIndex()) {
            val y = lineHeight * (index + 1)
            g2.color = Color.WHITE
            g2.drawString(line, 5, y + 5)
        }

        val origin = PointAbsolute(overlay.gameInfo.gameBounds.x, overlay.gameInfo.gameBounds.y)
        val absCenter = ConverterUtil.toPointAbsolute(
            overlay.gameInfo, hoveredCell.getCenter()
        ).getDifference(origin)
        g.drawImage(infoWindow, absCenter.x, absCenter.y, null)
    }

}