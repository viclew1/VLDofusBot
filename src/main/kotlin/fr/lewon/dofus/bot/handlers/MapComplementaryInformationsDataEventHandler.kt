package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object MapComplementaryInformationsDataEventHandler :
    AbstractMapComplementaryInformationsDataEventHandler<MapComplementaryInformationsDataMessage>() {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataMessage, connection: DofusConnection) {
        super.onEventReceived(socketResult, connection)
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.logger.debug("Moved to map [${socketResult.map.posX},${socketResult.map.posY}] ; World : ${socketResult.map.worldMap}")
    }
}