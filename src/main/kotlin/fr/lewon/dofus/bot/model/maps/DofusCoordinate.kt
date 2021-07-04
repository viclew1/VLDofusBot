package fr.lewon.dofus.bot.model.maps

import kotlin.math.abs

data class DofusCoordinate(val x: Int, val y: Int) {

    fun distanceTo(coordinate: DofusCoordinate): Int {
        return abs(x - coordinate.x) + abs(y - coordinate.y)
    }

}