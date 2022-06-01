package fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.spells.visual

import fr.lewon.dofus.bot.core.model.spell.DofusEffectZone
import fr.lewon.dofus.bot.core.model.spell.DofusEffectZoneType
import fr.lewon.dofus.bot.core.model.spell.DofusSpellLevel
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.game.fight.ai.EffectZoneCalculator
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AreaVisualPanel(var spell: DofusSpellLevel? = null) : JPanel() {

    companion object {
        private val BOARD = DofusBoard(50, 50).also {
            for (cell in it.cells) {
                cell.cellData.los = true
                cell.cellData.mov = true
            }
        }
        private val BOARD_MIN_ROW = BOARD.cells.minByOrNull { it.row }?.row ?: 0
        private val BOARD_MAX_ROW = BOARD.cells.maxByOrNull { it.row }?.row ?: 0
        private val BOARD_MIN_COL = BOARD.cells.minByOrNull { it.col }?.col ?: 0
        private val BOARD_MAX_COL = BOARD.cells.maxByOrNull { it.col }?.col ?: 0
        private val BOARD_CENTER_COL = BOARD_MIN_COL + (BOARD_MAX_COL - BOARD_MIN_COL) / 2
        private val BOARD_CENTER_ROW = BOARD_MIN_ROW + (BOARD_MAX_ROW - BOARD_MIN_ROW) / 2
        private val ORIGIN_CELL = BOARD.getCell(BOARD_CENTER_COL, BOARD_CENTER_ROW)
            ?: error("Cell should be present in board")
    }

    private val effectZoneCalculator = EffectZoneCalculator(BOARD)
    private var hoveredRow = Short.MAX_VALUE.toInt()
    private var hoveredCol = Short.MAX_VALUE.toInt()
    private val hoveredCells = ArrayList<DofusCell>()
    private val hoverLock = ReentrantLock()

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseExited(e: MouseEvent?) {
                hoveredCol = Short.MAX_VALUE.toInt()
                hoveredRow = Short.MAX_VALUE.toInt()
            }
        })
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                try {
                    hoverLock.lock()
                    hoveredCol = spell?.let { getIndex(it, e.x) } ?: Short.MAX_VALUE.toInt()
                    hoveredRow = spell?.let { getIndex(it, e.y) } ?: Short.MAX_VALUE.toInt()
                    val effectZone = spell?.effects?.lastOrNull()?.rawZone
                        ?: DofusEffectZone(DofusEffectZoneType.POINT, 1)
                    val toCell = BOARD.getCell(BOARD_CENTER_COL + hoveredCol, BOARD_CENTER_ROW + hoveredRow)
                    hoveredCells.clear()
                    if (toCell != null) {
                        val affectedCellIds = effectZoneCalculator.getAffectedCells(
                            ORIGIN_CELL.cellId, toCell.cellId, effectZone
                        )
                        val affectedCells = affectedCellIds.map { BOARD.getCell(it) }
                        hoveredCells.addAll(affectedCells)
                    }
                } finally {
                    hoverLock.unlock()
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.updateUI()
        val spell = spell ?: return
        val maxIndex = getMaxIndex(spell)
        val cellWidth = width / (maxIndex * 2 + 1)
        val cellHeight = height / (maxIndex * 2 + 1)
        val oldColor = g.color
        g.color = Color.LIGHT_GRAY
        g.fillRect(0, 0, width, height)
        for (row in -maxIndex..maxIndex) {
            g.color = Color.BLACK
            paintRow(g, spell, row, maxIndex, cellWidth, cellHeight)
        }
        for (index in -maxIndex..maxIndex) {
            val visualIndex = getVisualIndex(index, maxIndex)
            g.color = Color.BLACK
            g.drawLine(0, visualIndex, width, visualIndex)
            g.drawLine(visualIndex, 0, visualIndex, height)
        }
        g.color = oldColor
    }

    private fun getMaxIndex(spell: DofusSpellLevel): Int {
        return min(15, max(3, spell.maxRange + 2))
    }

    private fun getVisualIndex(index: Int, maxIndex: Int): Int {
        return ((maxIndex + index) * width) / (maxIndex * 2 + 1)
    }

    private fun getIndex(spell: DofusSpellLevel, visualIndex: Int): Int? {
        val maxIndex = getMaxIndex(spell)
        val index = visualIndex * (maxIndex * 2 + 1) / width - maxIndex
        return index.takeIf { it in -maxIndex..maxIndex }
    }

    private fun paintRow(
        g: Graphics,
        spell: DofusSpellLevel,
        row: Int,
        maxIndex: Int,
        cellWidth: Int,
        cellHeight: Int
    ) {
        val y = getVisualIndex(row, maxIndex)
        for (col in -maxIndex..maxIndex) {
            val x = getVisualIndex(col, maxIndex)
            g.color = getCellColor(spell, row, col)
            if (isCellInAOE(spell, col, row)) {
                g.color = mixColors(Color.RED, g.color, 0.5f)
            }
            g.drawRect(x, y, cellWidth, cellHeight)
            g.fillRect(x, y, cellWidth, cellHeight)
        }
    }

    private fun getCellColor(spell: DofusSpellLevel, row: Int, col: Int): Color {
        if (col == 0 && row == 0) {
            return Color.BLUE
        }
        if (isSpellLineValid(spell, col, row)) {
            if (isCellInRange(spell, col, row)) {
                return Color.DARK_GRAY
            } else if (spell.rangeCanBeBoosted && isCellInExtendedRange(spell, col, row)) {
                return Color.GRAY
            }
        }
        return Color.LIGHT_GRAY
    }

    private fun isCellInRange(spell: DofusSpellLevel, col: Int, row: Int): Boolean {
        return abs(row) + abs(col) in spell.minRange..spell.maxRange
    }

    private fun isCellInExtendedRange(spell: DofusSpellLevel, col: Int, row: Int): Boolean {
        return abs(row) + abs(col) in spell.maxRange + 1..spell.maxRange + 3
    }

    private fun isCellInAOE(spell: DofusSpellLevel, col: Int, row: Int): Boolean {
        if (!canTargetHoveredCell(spell)) {
            return false
        }
        try {
            hoverLock.lock()
            return hoveredCells.contains(BOARD.getCell(BOARD_CENTER_COL + col, BOARD_CENTER_ROW + row))
        } finally {
            hoverLock.unlock()
        }
    }

    private fun canTargetHoveredCell(spell: DofusSpellLevel): Boolean {
        return (isCellInRange(spell, hoveredCol, hoveredRow)
                || spell.rangeCanBeBoosted && isCellInExtendedRange(spell, hoveredCol, hoveredRow))
                && isSpellLineValid(spell, hoveredCol, hoveredRow)
    }

    private fun isSpellLineValid(spell: DofusSpellLevel, col: Int, row: Int): Boolean {
        return !spell.castInLine && !spell.castInDiagonal
                || spell.castInLine && isCellInLine(col, row)
                || spell.castInDiagonal && isCellInDiagonal(col, row)
    }

    private fun isCellInLine(col: Int, row: Int): Boolean {
        return row == 0 || col == 0
    }

    private fun isCellInDiagonal(col: Int, row: Int): Boolean {
        return abs(row) == abs(col)
    }

    private fun mixColors(color1: Color, color2: Color, ratio: Float): Color {
        return Color(
            min(255, max(0, color1.red + ((color2.red - color1.red) * ratio).toInt())),
            min(255, max(0, color1.green + ((color2.green - color1.green) * ratio).toInt())),
            min(255, max(0, color1.blue + ((color2.blue - color1.blue) * ratio).toInt())),
        )
    }

}