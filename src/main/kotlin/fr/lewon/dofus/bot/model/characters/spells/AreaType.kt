package fr.lewon.dofus.bot.model.characters.spells

import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.DofusCell
import kotlin.math.abs
import kotlin.math.sign

enum class AreaType(private val areaCellsGetter: (DofusCell, DofusCell, Int, DofusBoard) -> List<DofusCell>) {
    CIRCLE({ _, toCell, areaSize, board ->
        val row = toCell.row
        val col = toCell.col
        val cells = ArrayList<DofusCell>()
        for (c in col - areaSize..col + areaSize) {
            for (r in row - areaSize..row + areaSize) {
                if (abs(c - col) + abs(r - row) <= areaSize) {
                    board.getCell(c, r)?.let { cells.add(it) }
                }
            }
        }
        cells
    }),
    SQUARE({ _, toCell, areaSize, board ->
        val row = toCell.row
        val col = toCell.col
        val cells = ArrayList<DofusCell>()
        for (c in col - areaSize..col + areaSize) {
            for (r in row - areaSize..row + areaSize) {
                board.getCell(c, r)?.let { cells.add(it) }
            }
        }
        cells
    }),
    CROSS({ _, toCell, areaSize, board ->
        val row = toCell.row
        val col = toCell.col
        val cells = ArrayList<DofusCell>()
        for (i in 0..areaSize) {
            board.getCell(col, row - i)?.let { cells.add(it) }
            board.getCell(col, row + i)?.let { cells.add(it) }
            board.getCell(col - i, row)?.let { cells.add(it) }
            board.getCell(col + i, row)?.let { cells.add(it) }
        }
        cells
    }),
    LINE({ fromCell, toCell, areaSize, board ->
        val cells = ArrayList<DofusCell>()
        val dCol = toCell.col - fromCell.col
        val dRow = toCell.row - fromCell.row
        val sDCol = dCol.sign
        val sDRow = dRow.sign
        val absDCol = abs(dCol)
        val absDRow = abs(dRow)
        for (i in 0..areaSize) {
            if (absDCol > absDRow) {
                board.getCell(toCell.col + sDCol * i, toCell.row)?.let { cells.add(it) }
            } else if (absDCol < absDRow) {
                board.getCell(toCell.col, toCell.row + sDRow * i)?.let { cells.add(it) }
            } else {
                board.getCell(toCell.col + sDCol * i, toCell.row + sDRow * i)?.let { cells.add(it) }
            }
        }
        cells
    }),
    PERPENDICULAR_LINE({ fromCell, toCell, areaSize, board ->
        val cells = ArrayList<DofusCell>()
        val dCol = toCell.col - fromCell.col
        val dRow = toCell.row - fromCell.row
        val sDCol = dCol.sign
        val sDRow = dRow.sign
        val absDCol = abs(dCol)
        val absDRow = abs(dRow)
        for (i in 0..areaSize) {
            if (absDCol > absDRow) {
                board.getCell(toCell.col, toCell.row + i)?.let { cells.add(it) }
                board.getCell(toCell.col, toCell.row - i)?.let { cells.add(it) }
            } else if (absDCol < absDRow) {
                board.getCell(toCell.col + i, toCell.row)?.let { cells.add(it) }
                board.getCell(toCell.col - i, toCell.row)?.let { cells.add(it) }
            } else {
                board.getCell(toCell.col + i, toCell.row - (sDRow * sDCol) * i)?.let { cells.add(it) }
                board.getCell(toCell.col - i, toCell.row + (sDRow * sDCol) * i)?.let { cells.add(it) }
            }
        }
        cells
    });

    fun getAreaCells(board: DofusBoard, fromCell: DofusCell, toCell: DofusCell, areaSize: Int): List<DofusCell> {
        return areaCellsGetter(fromCell, toCell, areaSize, board)
    }
}