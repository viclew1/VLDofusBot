package fr.lewon.dofus.bot.util.fight

import java.awt.Rectangle

class FightCell(val bounds: Rectangle) {

    val neighbors = ArrayList<FightCell>()

    fun getCenter(): Pair<Int, Int> {
        return Pair(bounds.centerX.toInt(), bounds.centerY.toInt())
    }

}