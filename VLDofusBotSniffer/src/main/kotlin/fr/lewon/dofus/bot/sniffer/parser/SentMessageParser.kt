package fr.lewon.dofus.bot.sniffer.parser

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class SentMessageParser(state: MessageParserState) : MessageParser(PacketOrigin.SENT, state) {

    override fun readMessageLength(header: Int, lengthType: Int, src: ByteArrayReader): Int {
        src.readInt()
        return when (lengthType) {
            0 -> 0
            1 -> src.readUnsignedByte()
            2 -> src.readUnsignedShort()
            3 -> ((src.readUnsignedByte() and 255) shl 16) + src.readUnsignedShort()
            else -> error("Invalid length")
        }
    }

}