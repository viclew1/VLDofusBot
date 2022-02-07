package fr.lewon.dofus.bot.gui.overlay

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.ai.DangerMap
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

object LOSHelper : AbstractOverlay(LOSHelperPanel) {

    private var hitBoxByCell: Map<DofusCell, Polygon> = HashMap()
    private lateinit var gameInfo: GameInfo
    private var dangerByCell: HashMap<Int, Int> = HashMap()

    override fun updateOverlay(gameInfo: GameInfo) {
        this.gameInfo = gameInfo
        JNAUtil.updateGameBounds(gameInfo)
        val windowPos = JNAUtil.getGamePosition(gameInfo.connection.pid)
        val gameBounds = gameInfo.gameBounds
        bounds = Rectangle(gameBounds.x + windowPos.x, gameBounds.y + windowPos.y, gameBounds.width, gameBounds.height)
        contentPane.size = Dimension(gameInfo.gameBounds.width, gameInfo.gameBounds.height)
        hitBoxByCell = gameInfo.dofusBoard.cells.associateWith { buildCellHitBox(it) }
    }

    fun updateDangerMap(dangerMap: DangerMap) {
        this.dangerByCell = HashMap()
        dangerMap.values.flatMap { it.entries }.forEach {
            val currentDanger = dangerByCell[it.key] ?: 0
            val addedDanger = it.value
            dangerByCell[it.key] = currentDanger + addedDanger
        }
    }

    private object LOSHelperPanel : JPanel() {

        var hoveredCell: DofusCell? = null
        var losOkCells: List<DofusCell> = ArrayList()

        init {
            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseMoved(e: MouseEvent) {
                    updateHighlightedCells(e.point)
                }

                override fun mouseDragged(e: MouseEvent) {
                    mouseMoved(e)
                }
            })
            addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(e: MouseEvent) {
                    if (e.button == MouseEvent.BUTTON1) {
                        sendClickToGame(e.point)
                    }
                }
            })
        }

        private fun sendClickToGame(mouseLocation: Point) {
            val absoluteLocation = PointAbsolute(
                mouseLocation.x + gameInfo.gameBounds.x,
                mouseLocation.y + gameInfo.gameBounds.y
            )
            MouseUtil.leftClick(gameInfo, absoluteLocation)
        }

        fun updateHighlightedCells(mouseLocation: Point) {
            hoveredCell = getCellAtLocation(mouseLocation)
                ?.takeIf { it.isAccessible() }
            losOkCells = hoveredCell?.let {
                gameInfo.dofusBoard.cells.filter { c -> c.isAccessible() && gameInfo.fightBoard.lineOfSight(it, c) }
            } ?: emptyList()
        }

        private fun getCellAtLocation(location: Point): DofusCell? {
            return hitBoxByCell.entries.firstOrNull { it.value.contains(location) }?.key
        }

        override fun paintComponent(g: Graphics) {
            rootPane.updateUI()
            super.paintComponent(g)
            val holes = gameInfo.dofusBoard.cells.filter { it.isHole() }
            val walls = gameInfo.dofusBoard.cells.filter { it.isWall() }.sortedBy { it.cellId }
            val accessibleCells = gameInfo.dofusBoard.cells.filter { it.isAccessible() }
            accessibleCells.forEach { drawAccessibleCell(g, it) }
            holes.forEach { drawHole(g, it) }
            walls.forEach { drawWall(g, it) }
        }

        private fun drawLine(g: Graphics, from: PointRelative, to: PointRelative) {
            val origin = PointAbsolute(gameInfo.gameBounds.x, gameInfo.gameBounds.y)
            val fromAbsolute = ConverterUtil.toPointAbsolute(gameInfo, from).getDifference(origin)
            val toAbsolute = ConverterUtil.toPointAbsolute(gameInfo, to).getDifference(origin)
            g.drawLine(fromAbsolute.x, fromAbsolute.y, toAbsolute.x, toAbsolute.y)
        }

        private fun drawHole(g: Graphics, cell: DofusCell) {
            val polygon = hitBoxByCell[cell] ?: return
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
            val polygon = buildPolygon(
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
            val fighter = gameInfo.fightBoard.getFighter(cell)
            val isCellEnemy = fighter?.let { gameInfo.fightBoard.isFighterEnemy(it) } == true
            val isCellAlly = fighter?.let { gameInfo.fightBoard.isFighterEnemy(it) } == false
            val color = when {
                hoveredCell == cell -> Color.CYAN
                losOkCells.contains(cell) -> Color.BLUE
                isCellEnemy -> Color.RED
                isCellAlly -> Color.GREEN
                else -> Color.LIGHT_GRAY
            }
            val polygon = hitBoxByCell[cell] ?: return
            val oldColor = g.color
            g.color = color
            g.fillPolygon(polygon)
            if (isCellAlly || isCellEnemy) {
                val center = cell.getCenter()
                val halfCellPolygon = buildPolygon(
                    listOf(
                        PointRelative(center.x, center.y - cell.bounds.height),
                        PointRelative(center.x + cell.bounds.width, center.y),
                        PointRelative(center.x, center.y + cell.bounds.height),
                    )
                )
                g.color = if (isCellAlly) Color.GREEN else Color.RED
                g.fillPolygon(halfCellPolygon)
            }
            g.color = Color.BLACK
            val danger = dangerByCell[cell.cellId] ?: 0
            val origin = PointAbsolute(gameInfo.gameBounds.x, gameInfo.gameBounds.y)
            val textLocRelative = PointRelative(cell.getCenter().x - cell.bounds.width / 2, cell.getCenter().y)
            val absCenter = ConverterUtil.toPointAbsolute(gameInfo, textLocRelative).getDifference(origin)
            (g as Graphics2D).drawString("$danger", absCenter.x, absCenter.y + g.fontMetrics.height / 3)
            g.color = Color.WHITE
            g.drawPolygon(polygon)
            g.color = oldColor
        }

    }

    private fun buildCellHitBox(cell: DofusCell): Polygon {
        val center = cell.getCenter()
        return buildPolygon(
            listOf(
                PointRelative(center.x, center.y - cell.bounds.height),
                PointRelative(center.x + cell.bounds.width, center.y),
                PointRelative(center.x, center.y + cell.bounds.height),
                PointRelative(center.x - cell.bounds.width, center.y)
            )
        )
    }

    private fun buildPolygon(pointRelatives: List<PointRelative>): Polygon {
        val origin = PointAbsolute(gameInfo.gameBounds.x, gameInfo.gameBounds.y)
        val absolutePoints = pointRelatives.map { ConverterUtil.toPointAbsolute(gameInfo, it).getDifference(origin) }
        val xPoints = absolutePoints.map { it.x }.toIntArray()
        val yPoints = absolutePoints.map { it.y }.toIntArray()
        return Polygon(xPoints, yPoints, absolutePoints.size)
    }

}