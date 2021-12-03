package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object MapComplementaryInformationsDataInHavenBagEventHandler :
    EventHandler<MapComplementaryInformationsDataInHavenBagMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataInHavenBagMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        gameInfo.currentMap = socketResult.map
        gameInfo.entityPositionsOnMapByEntityId.clear()
        socketResult.actors.forEach {
            gameInfo.entityPositionsOnMapByEntityId[it.contextualId] = it.disposition.cellId
        }
        VldbLogger.debug("Character in haven bag")
    }

}