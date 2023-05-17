package fr.lewon.dofus.bot.sniffer.parser

enum class PacketOrigin(val buildMessageParser: (state: MessageParserState) -> MessageParser) {
    SENT({ SentMessageParser(it) }),
    RECEIVED({ ReceivedMessageParser(it) })
}