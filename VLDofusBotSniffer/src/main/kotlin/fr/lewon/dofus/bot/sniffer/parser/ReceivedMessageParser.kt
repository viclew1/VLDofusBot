package fr.lewon.dofus.bot.sniffer.parser

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class ReceivedMessageParser(state: MessageParserState) : MessageParser(PacketOrigin.RECEIVED, state) {

    override fun readMessageLength(header: Int, lengthType: Int, src: ByteArrayReader): Int = when (lengthType) {
        0 -> 0
        1 -> src.readUnsignedByte()
        2 -> src.readUnsignedShort()
        3 -> ((src.readUnsignedByte() and 255) shl 16) + ((src.readUnsignedByte() and 255) shl 8) + (src.readUnsignedByte() and 255)
        else -> error("Invalid length")
    }

}