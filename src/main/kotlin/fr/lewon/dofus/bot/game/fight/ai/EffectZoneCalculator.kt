package fr.lewon.dofus.bot.game.fight.ai

import fr.lewon.dofus.bot.core.model.spell.DofusEffectZone
import fr.lewon.dofus.bot.core.model.spell.DofusEffectZoneType
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
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
        val cells = ArrayList<DofusCell>()
        when (effectZone.effectZoneType) {
            DofusEffectZoneType.POINT -> cells.add(targetCell)
            DofusEffectZoneType.CIRCLE -> {
                for (c in col - areaSize..col + areaSize) {
                    for (r in row - areaSize..row + areaSize) {
                        if (abs(c - col) + abs(r - row) <= areaSize) {
                            dofusBoard.getCell(c, r)?.let { cells.add(it) }
                        }
                    }
                }
            }
            DofusEffectZoneType.CROSS, DofusEffectZoneType.CROSS_FROM_TARGET -> {
                for (i in 0..areaSize) {
                    dofusBoard.getCell(col, row - i)?.let { cells.add(it) }
                    dofusBoard.getCell(col, row + i)?.let { cells.add(it) }
                    dofusBoard.getCell(col - i, row)?.let { cells.add(it) }
                    dofusBoard.getCell(col + i, row)?.let { cells.add(it) }
                }
            }
            DofusEffectZoneType.DIAGONAL_CROSS -> {
                for (i in 0..areaSize) {
                    dofusBoard.getCell(col - i, row - i)?.let { cells.add(it) }
                    dofusBoard.getCell(col - i, row + i)?.let { cells.add(it) }
                    dofusBoard.getCell(col + i, row - i)?.let { cells.add(it) }
                    dofusBoard.getCell(col + i, row + i)?.let { cells.add(it) }
                }
            }
            DofusEffectZoneType.LINE -> {
                if (fromCellId == targetCellId) {
                    return emptyList()
                }
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
                    cell?.let { cells.add(it) } ?: break
                }
            }
            DofusEffectZoneType.PERPENDICULAR_LINE -> {
                if (fromCellId == targetCellId) {
                    return emptyList()
                }
                val dCol = col - fromCell.col
                val dRow = row - fromCell.row
                val sDCol = dCol.sign
                val sDRow = dRow.sign
                val absDCol = abs(dCol)
                val absDRow = abs(dRow)
                for (i in 0..areaSize) {
                    if (absDCol > absDRow) {
                        dofusBoard.getCell(col, row + i)?.let { cells.add(it) }
                        dofusBoard.getCell(col, row - i)?.let { cells.add(it) }
                    } else if (absDCol < absDRow) {
                        dofusBoard.getCell(col + i, row)?.let { cells.add(it) }
                        dofusBoard.getCell(col - i, row)?.let { cells.add(it) }
                    } else {
                        dofusBoard.getCell(col + i, row - (sDRow * sDCol) * i)?.let { cells.add(it) }
                        dofusBoard.getCell(col - i, row + (sDRow * sDCol) * i)?.let { cells.add(it) }
                    }
                }
            }
        }
        return cells.filter { it.isAccessible() }
            .map { it.cellId }
    }
}