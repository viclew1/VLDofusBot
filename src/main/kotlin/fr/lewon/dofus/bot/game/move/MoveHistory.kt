package fr.lewon.dofus.bot.game.move

import fr.lewon.dofus.bot.model.maps.DofusMap
import java.util.*
import kotlin.collections.ArrayList

object MoveHistory {

    private val lastMoves = Collections.synchronizedList(ArrayList<Move>())

    fun getLastMove(): Move? {
        if (lastMoves.isEmpty()) {
            return null
        }
        return lastMoves.removeAt(lastMoves.size - 1)
    }

    @Synchronized
    fun addMove(direction: Direction, fromMap: DofusMap, toMap: DofusMap) {
        lastMoves.add(Move(direction, fromMap, toMap))
        while (lastMoves.size > 10) {
            lastMoves.removeAt(0)
        }
    }
}