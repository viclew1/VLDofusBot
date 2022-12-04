package fr.lewon.dofus.bot.gui2.main.exploration.map.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.CELL_SIZE
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.MAX_ZOOM
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.maxPosX
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.maxPosY
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.minPosX
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.minPosY
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.totalHeight
import fr.lewon.dofus.bot.gui2.main.exploration.ExplorationUIUtil.totalWidth
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

val WorldMapHelperOverlay = buildOverlay().toPainter()

private fun buildOverlay(): BufferedImage {
    val allMaps = MapManager.getAllMaps().filter { it.isInOverlay() }
    return buildOverlayImage { graphics, x, y ->
        if (allMaps.any { it.posX == x && it.posY == y }) {
            val drawX = x - minPosX
            val drawY = y - minPosY
            val topLeft = Offset(drawX * CELL_SIZE, drawY * CELL_SIZE)
            val topRight = Offset((drawX + 1) * CELL_SIZE, drawY * CELL_SIZE)
            val bottomLeft = Offset(drawX * CELL_SIZE, (drawY + 1) * CELL_SIZE)
            val bottomRight = Offset((drawX + 1) * CELL_SIZE, (drawY + 1) * CELL_SIZE)
            if (allMaps.none { it.posX == x - 1 && it.posY == y }) {
                graphics.drawCellLine(topLeft, bottomLeft, 1f, Color.BLACK)
            }
            if (allMaps.none { it.posX == x && it.posY == y + 1 }) {
                graphics.drawCellLine(bottomLeft, bottomRight, 1f, Color.BLACK)
            }
            if (allMaps.none { it.posX == x + 1 && it.posY == y }) {
                graphics.drawCellLine(bottomRight, topRight, 1f, Color.BLACK)
            }
            if (allMaps.none { it.posX == x && it.posY == y - 1 }) {
                graphics.drawCellLine(topRight, topLeft, 1f, Color.BLACK)
            }
        }
    }
}

fun DofusMap.isInOverlay(): Boolean =
    worldMap?.id == 1 && (subArea.capturable || subArea.displayOnWorldMap)

fun buildOverlayImage(drawCell: (graphics2D: Graphics2D, x: Int, y: Int) -> Unit): BufferedImage =
    BufferedImage((totalWidth * MAX_ZOOM).toInt(), (totalHeight * MAX_ZOOM).toInt(), BufferedImage.TYPE_INT_ARGB).also {
        val g2 = it.graphics as Graphics2D
        for (x in minPosX..maxPosX) {
            for (y in minPosY..maxPosY) {
                drawCell(g2, x, y)
            }
        }
    }

fun Graphics2D.drawCellLine(from: Offset, to: Offset, strokeSize: Float, color: Color) {
    this.stroke = BasicStroke(strokeSize * MAX_ZOOM)
    this.color = color
    drawLine(
        (from.x * MAX_ZOOM).toInt(), (from.y * MAX_ZOOM).toInt(),
        (to.x * MAX_ZOOM).toInt(), (to.y * MAX_ZOOM).toInt()
    )
}