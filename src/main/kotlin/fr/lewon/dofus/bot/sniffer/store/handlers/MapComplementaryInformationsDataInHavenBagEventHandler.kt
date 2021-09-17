package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.ui.ConsoleLogger

object MapComplementaryInformationsDataInHavenBagEventHandler :
    EventHandler<MapComplementaryInformationsDataInHavenBagMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataInHavenBagMessage) {
        GameInfo.currentMap = socketResult.dofusMap
        GameInfo.inHavenBag = true
        ConsoleLogger.info("Character in haven bag")
    }

}