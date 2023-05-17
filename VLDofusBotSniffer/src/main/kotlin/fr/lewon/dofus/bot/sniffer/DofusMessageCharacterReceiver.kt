package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.sniffer.parser.MessageParser
import fr.lewon.dofus.bot.sniffer.parser.MessageParserState
import fr.lewon.dofus.bot.sniffer.parser.PacketOrigin
import fr.lewon.dofus.bot.sniffer.store.EventStore
import org.pcap4j.packet.TcpPacket

class DofusMessageCharacterReceiver(connection: DofusConnection, eventStore: EventStore, logger: VldbLogger) {

    private val messageParserByOrigin = PacketOrigin.values().associateWith {
        it.buildMessageParser(MessageParserState(connection, eventStore, logger))
    }

    fun treatReceivedTcpPacket(tcpPacket: TcpPacket) {
        treatTcpPacket(tcpPacket, PacketOrigin.RECEIVED)
    }

    fun treatSentTcpPacket(tcpPacket: TcpPacket) {
        treatTcpPacket(tcpPacket, PacketOrigin.SENT)
    }

    private fun treatTcpPacket(tcpPacket: TcpPacket, origin: PacketOrigin) {
        getMessageParser(origin).receivePacket(tcpPacket)
    }

    private fun getMessageParser(origin: PacketOrigin): MessageParser {
        return messageParserByOrigin[origin] ?: error("No parser for origin : ${origin.name}")
    }

}