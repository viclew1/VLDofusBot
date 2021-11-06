package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object MapComplementaryInformationsDataInHavenBagEventHandler :
    EventHandler<MapComplementaryInformationsDataInHavenBagMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataInHavenBagMessage) {
        GameInfo.currentMap = socketResult.map
        GameInfo.inHavenBag = true
        GameInfo.entityPositionsOnMapByEntityId.clear()
        socketResult.actors.forEach {
            GameInfo.entityPositionsOnMapByEntityId[it.contextualId] = it.disposition.cellId
        }
        VldbLogger.debug("Character in haven bag")
    }

}