package fr.lewon.dofus.bot.handlers.movement

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object MapComplementaryInformationsDataEventHandler :
    AbstractMapComplementaryInformationsDataEventHandler<MapComplementaryInformationsDataMessage>() {

    override fun onEventReceived(
        socketResult: MapComplementaryInformationsDataMessage,
        connection: DofusConnection
    ) {
        super.onEventReceived(socketResult, connection)
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.isInHavenBag = false
    }
}