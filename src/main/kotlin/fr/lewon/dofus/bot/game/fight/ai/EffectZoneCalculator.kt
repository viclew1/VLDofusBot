package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusEffectZone
import fr.lewon.dofus.bot.core.model.spell.DofusEffectZoneType
import fr.lewon.dofus.bot.game.DofusBoard
import kotlin.math.abs
import kotlin.math.sign

class EffectZoneCalculator(private val dofusBoard: DofusBoard) {

    fun getAffectedCells(
        fromCellId: Int,
        targetCellId: Int,
        effectZones: List<DofusEffectZone>
    ): List<Int> {
        return effectZones.flatMap { getAffectedCells(fromCellId, targetCellId, it) }
    }

    fun getAffectedCells(
        fromCellId: Int,
        targetCellId: Int,
        effectZone: DofusEffectZone
    ): List<Int> {
        val areaSize = effectZone.size
        val fromCell = dofusBoard.getCell(fromCellId)
        val targetCell = dofusBoard.getCell(targetCellId)
        val row = targetCell.row
        val col = targetCell.col
        return when (effectZone.effectZoneType) {
            DofusEffectZoneType.POINT -> listOf(targetCellId)
            DofusEffectZoneType.CIRCLE -> {
                val cells = ArrayList<Int>()
                for (c in col - areaSize..col + areaSize) {
                    for (r in row - areaSize..row + areaSize) {
                        if (abs(c - col) + abs(r - row) <= areaSize) {
                            dofusBoard.getCell(c, r)?.let { cells.add(it.cellId) }
                        }
                    }
                }
                cells
            }
            DofusEffectZoneType.CROSS, DofusEffectZoneType.CROSS_FROM_TARGET -> {
                val cells = ArrayList<Int>()
                for (i in 0..areaSize) {
                    dofusBoard.getCell(col, row - i)?.let { cells.add(it.cellId) }
                    dofusBoard.getCell(col, row + i)?.let { cells.add(it.cellId) }
                    dofusBoard.getCell(col - i, row)?.let { cells.add(it.cellId) }
                    dofusBoard.getCell(col + i, row)?.let { cells.add(it.cellId) }
                }
                cells
            }
            DofusEffectZoneType.DIAGONAL_CROSS -> {
                val cells = ArrayList<Int>()
                for (i in 0..areaSize) {
                    dofusBoard.getCell(col - i, row - i)?.let { cells.add(it.cellId) }
                    dofusBoard.getCell(col - i, row + i)?.let { cells.add(it.cellId) }
                    dofusBoard.getCell(col + i, row - i)?.let { cells.add(it.cellId) }
                    dofusBoard.getCell(col + i, row + i)?.let { cells.add(it.cellId) }
                }
                cells
            }
            DofusEffectZoneType.LINE -> {
                if (fromCellId == targetCellId) {
                    return emptyList()
                }
                val cells = ArrayList<Int>()
                val dCol = col - fromCell.col
                val dRow = row - fromCell.row
                val sDCol = dCol.sign
                val sDRow = dRow.sign
                val absDCol = abs(dCol)
                val absDRow = abs(dRow)
                for (i in 0..areaSize) {
                    val cell = when {
                        absDCol > absDRow -> dofusBoard.getCell(col + sDCol * i, row)
                        absDCol < absDRow -> dofusBoard.getCell(col, row + sDRow * i)
                        else -> dofusBoard.getCell(col + sDCol * i, row + sDRow * i)
                    }
                    cell?.let { cells.add(it.cellId) } ?: break
                }
                cells
            }
            DofusEffectZoneType.PERPENDICULAR_LINE -> {
                if (fromCellId == targetCellId) {
                    return emptyList()
                }
                val cells = ArrayList<Int>()
                val dCol = col - fromCell.col
                val dRow = row - fromCell.row
                val sDCol = dCol.sign
                val sDRow = dRow.sign
                val absDCol = abs(dCol)
                val absDRow = abs(dRow)
                for (i in 0..areaSize) {
                    if (absDCol > absDRow) {
                        dofusBoard.getCell(col, row + i)?.let { cells.add(it.cellId) }
                        dofusBoard.getCell(col, row - i)?.let { cells.add(it.cellId) }
                    } else if (absDCol < absDRow) {
                        dofusBoard.getCell(col + i, row)?.let { cells.add(it.cellId) }
                        dofusBoard.getCell(col - i, row)?.let { cells.add(it.cellId) }
                    } else {
                        dofusBoard.getCell(col + i, row - (sDRow * sDCol) * i)?.let { cells.add(it.cellId) }
                        dofusBoard.getCell(col - i, row + (sDRow * sDCol) * i)?.let { cells.add(it.cellId) }
                    }
                }
                cells
            }
        }
    }
}