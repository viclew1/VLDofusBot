package fr.lewon.dofus.bot.game.fight

import java.awt.Rectangle

class FightCell(val row: Int, val col: Int, val bounds: Rectangle, val fightCellType: FightCellType) {

    val neighbors = ArrayList<FightCell>()

    fun getCenter(): Pair<Int, Int> {
        return Pair(bounds.centerX.toInt(), bounds.centerY.toInt())
    }

}