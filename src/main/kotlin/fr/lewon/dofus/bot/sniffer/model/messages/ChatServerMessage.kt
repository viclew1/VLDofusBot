package fr.lewon.dofus.bot.sniffer.model.messages

import fr.lewon.dofus.bot.sniffer.game.chat.DofusChannel
import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class ChatServerMessage : INetworkType {

    lateinit var pseudo: String
    lateinit var channel: DofusChannel
    lateinit var text: String

    override fun deserialize(stream: ByteArrayReader) {
        channel = DofusChannel.fromByte(stream.readByte())

        text = stream.readUTF()
        stream.skip(4)

        val lengthFiller: Int = stream.readShort()
        stream.skip(lengthFiller)
        stream.skip(8)

        pseudo = stream.readUTF()
    }

}