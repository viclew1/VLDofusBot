package fr.lewon.dofus.bot.game.move

import java.util.concurrent.LinkedBlockingDeque

class MoveHistory {

    private val lastMoves = LinkedBlockingDeque<Double>(100)

    fun pollLastMap(): Double? {
        return lastMoves.pollLast()
    }

    @Synchronized
    fun addMap(mapId: Double) {
        if (!lastMoves.offer(mapId)) {
            lastMoves.poll()
            lastMoves.offer(mapId)
        }
    }
}