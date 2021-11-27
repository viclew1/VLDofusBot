package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.model.messages.fight.CharacterStatsListMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object CharacterStatsListEventHandler : EventHandler<CharacterStatsListMessage> {

    override fun onEventReceived(socketResult: CharacterStatsListMessage, snifferId: Long) {
        val gameInfo = GameSnifferUtil.getGameInfoBySnifferId(snifferId)
        val characteristics = socketResult.stats.characteristics
        gameInfo.playerBaseCharacteristics = characteristics.associateBy { it.characteristicId }
        VldbLogger.debug("Player base characteristics updated")
    }

}