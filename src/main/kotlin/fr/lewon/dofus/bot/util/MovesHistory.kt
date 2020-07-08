package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import java.util.*

object MovesHistory {

    private val previousMoves = LinkedList<Directions>()

    fun addMove(move: Directions) {
        previousMoves.add(move)
        if (previousMoves.size == 10) {
            previousMoves.removeFirst()
        }
    }

    fun cancelLastMove(controller: DofusTreasureBotGUIController, logItem: LogItem?): Boolean {
        return previousMoves.pollLast()
            ?.getReverseDir()
            ?.buildMoveTask(controller, logItem)
            ?.run()
            ?.let { previousMoves.removeLast() } != null
    }

    fun clearHistory() {
        previousMoves.clear()
    }

}