package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object TreasureHuntEventHandler : EventHandler<TreasureHuntMessage> {

    override fun onEventReceived(socketResult: TreasureHuntMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val oldTreasureHunt = gameInfo.treasureHunt
        gameInfo.treasureHunt = socketResult
        if (oldTreasureHunt != null && oldTreasureHunt.checkPointCurrent < socketResult.checkPointCurrent) {
            HintManager.updateHints(oldTreasureHunt)
        }
    }

}