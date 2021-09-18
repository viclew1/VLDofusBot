package fr.lewon.dofus.bot.game.move

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.model.move.Direction
import fr.lewon.dofus.bot.model.move.Move
import java.util.concurrent.LinkedBlockingDeque

object MoveHistory {

    private val lastMoves = LinkedBlockingDeque<Move>(10)

    fun getLastMove(): Move? {
        return lastMoves.pollLast()
    }

    @Synchronized
    fun addMove(direction: Direction, fromMap: DofusMap, toMap: DofusMap) {
        val move = Move(direction, fromMap, toMap)
        if (!lastMoves.offer(move)) {
            lastMoves.poll()
            lastMoves.offer(move)
        }
    }
}