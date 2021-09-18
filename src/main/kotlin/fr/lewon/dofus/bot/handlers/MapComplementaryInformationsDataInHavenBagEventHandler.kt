package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.logs.VldbLogger

object MapComplementaryInformationsDataInHavenBagEventHandler :
    EventHandler<MapComplementaryInformationsDataInHavenBagMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataInHavenBagMessage) {
        GameInfo.currentMap = socketResult.map
        GameInfo.inHavenBag = true
        VldbLogger.info("Character in haven bag")
    }

}