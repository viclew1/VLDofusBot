package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.CharacterStatsListMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object CharacterStatsListEventHandler : EventHandler<CharacterStatsListMessage> {

    override fun onEventReceived(socketResult: CharacterStatsListMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        val characteristics = socketResult.stats.characteristics
        gameInfo.playerBaseCharacteristics = characteristics.associateBy { it.characteristicId }
        gameInfo.logger.debug("Player base characteristics updated")
    }

}