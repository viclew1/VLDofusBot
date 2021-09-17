package fr.lewon.dofus.bot.model.maps

import kotlin.math.abs

data class DofusCoordinates(val x: Int, val y: Int) {

    fun distanceTo(coordinates: DofusCoordinates): Int {
        return abs(x - coordinates.x) + abs(y - coordinates.y)
    }

}