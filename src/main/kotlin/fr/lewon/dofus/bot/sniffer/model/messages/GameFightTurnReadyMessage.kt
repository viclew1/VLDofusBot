package fr.lewon.dofus.bot.sniffer.model.messages

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameFightTurnReadyMessage : INetworkType {
    override fun deserialize(stream: ByteArrayReader) {
    }
}