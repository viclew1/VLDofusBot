package fr.lewon.dofus.bot.sniffer.model.messages.chat

import fr.lewon.dofus.bot.sniffer.game.chat.DofusChannel
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader
import org.apache.commons.text.StringEscapeUtils

open class ChatServerMessage : INetworkMessage {

    lateinit var pseudo: String
    lateinit var channel: DofusChannel
    lateinit var text: String

    override fun deserialize(stream: ByteArrayReader) {
        channel = DofusChannel.fromByte(stream.readByte())

        text = stream.readUTF()
        text = StringEscapeUtils.unescapeHtml4(text)
        text = StringEscapeUtils.unescapeHtml4(text)
        stream.skip(4)

        val lengthFiller: Int = stream.readUnsignedShort()
        stream.skip(lengthFiller)
        stream.skip(8)

        pseudo = stream.readUTF()
    }

}