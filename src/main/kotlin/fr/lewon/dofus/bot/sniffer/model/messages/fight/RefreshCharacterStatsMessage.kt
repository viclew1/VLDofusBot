package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.charac.GameFightCharacteristics
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class RefreshCharacterStatsMessage : INetworkMessage {

    var fighterId = 0.0
    var stats = GameFightCharacteristics()

    override fun deserialize(stream: ByteArrayReader) {
        fighterId = stream.readDouble()
        stats.deserialize(stream)
    }
}