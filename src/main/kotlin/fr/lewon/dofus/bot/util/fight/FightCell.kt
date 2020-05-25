package fr.lewon.dofus.bot.util.fight

import java.awt.Rectangle

class FightCell(val row: Int, val col: Int, val bounds: Rectangle, val fightCellType: FightCellType) {

    val neighbors = ArrayList<FightCell>()

    fun getCenter(): Pair<Int, Int> {
        return Pair(bounds.centerX.toInt(), bounds.centerY.toInt())
    }

    fun deepCopy(): FightCell {
        return FightCell(row, col, Rectangle(bounds.x, bounds.y, bounds.width, bounds.height), fightCellType)
    }

}