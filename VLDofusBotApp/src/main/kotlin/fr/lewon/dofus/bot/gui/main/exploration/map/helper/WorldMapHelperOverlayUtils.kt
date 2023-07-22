package fr.lewon.dofus.bot.gui.main.exploration.map.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.CellSize
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.maxPosX
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.maxPosY
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.minPosX
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.minPosY
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.totalHeight
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil.totalWidth
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

val WorldMapHelperOverlay = buildOverlay().toPainter()

const val Scale = 4

private fun buildOverlay(): BufferedImage {
    val allMaps = MapManager.getAllMaps().filter { it.isInOverlay() }
    return buildOverlayImage { graphics, x, y ->
        if (allMaps.any { it.posX == x && it.posY == y }) {
            val drawX = x - minPosX
            val drawY = y - minPosY
            val topLeft = Offset(drawX * CellSize, drawY * CellSize)
            val topRight = Offset((drawX + 1) * CellSize, drawY * CellSize)
            val bottomLeft = Offset(drawX * CellSize, (drawY + 1) * CellSize)
            val bottomRight = Offset((drawX + 1) * CellSize, (drawY + 1) * CellSize)
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
    BufferedImage((totalWidth * Scale).toInt(), (totalHeight * Scale).toInt(), BufferedImage.TYPE_INT_ARGB).also {
        val g2 = it.graphics as Graphics2D
        for (x in minPosX..maxPosX) {
            for (y in minPosY..maxPosY) {
                drawCell(g2, x, y)
            }
        }
    }

fun Graphics2D.drawCellLine(from: Offset, to: Offset, strokeSize: Float, color: Color) {
    this.stroke = BasicStroke(strokeSize * Scale)
    this.color = color
    drawLine(
        (from.x * Scale).toInt(), (from.y * Scale).toInt(),
        (to.x * Scale).toInt(), (to.y * Scale).toInt()
    )
}