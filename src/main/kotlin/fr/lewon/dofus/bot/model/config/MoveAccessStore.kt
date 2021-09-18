package fr.lewon.dofus.bot.model.config

import fr.lewon.dofus.bot.model.move.Direction
import fr.lewon.dofus.bot.util.geometry.PointRelative

class MoveAccessStore : HashMap<Double, HashMap<Direction, PointRelative>>() {

    fun registerAccessPoint(mapId: Double, direction: Direction, movePoint: PointRelative) {
        val movePointByDirection = this.computeIfAbsent(mapId) { HashMap() }
        movePointByDirection[direction] = movePoint
    }

    fun getAccessPoint(mapId: Double, direction: Direction): PointRelative? {
        return this[mapId]?.get(direction)
    }

}