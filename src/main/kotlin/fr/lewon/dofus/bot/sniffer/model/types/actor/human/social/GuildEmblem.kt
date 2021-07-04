package fr.lewon.dofus.bot.sniffer.model.types.actor.human.social

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GuildEmblem : INetworkType {

    var symbolShape = -1
    var symbolColor = -1
    var backgroundShape = -1
    var backgroundColor = -1

    override fun deserialize(stream: ByteArrayReader) {
        symbolShape = stream.readVarShort()
        symbolColor = stream.readInt()
        backgroundShape = stream.readByte().toInt()
        backgroundColor = stream.readInt()
    }
}