package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object MapComplementaryInformationsDataInHavenBagEventHandler :
    AbstractMapComplementaryInformationsDataEventHandler<MapComplementaryInformationsDataInHavenBagMessage>() {

    override fun onEventReceived(
        socketResult: MapComplementaryInformationsDataInHavenBagMessage,
        connection: DofusConnection
    ) {
        super.onEventReceived(socketResult, connection)
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.logger.debug("Character in haven bag")
    }

}