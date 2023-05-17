package fr.lewon.dofus.bot.sniffer.parser

import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.DofusMessagePremise
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import fr.lewon.dofus.bot.sniffer.exceptions.AddToStoreFailedException
import fr.lewon.dofus.bot.sniffer.exceptions.IncompleteMessageException
import fr.lewon.dofus.bot.sniffer.exceptions.ParseFailedException
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import org.apache.commons.codec.binary.Hex
import org.pcap4j.packet.TcpPacket

abstract class MessageParser(private val packetOrigin: PacketOrigin, private val state: MessageParserState) {

    companion object {
        const val BIT_RIGHT_SHIFT_LEN_PACKET_ID = 2
        const val BIT_MASK = 3
    }

    private val objectMapper = ObjectMapper()
    private val packets = ArrayList<TcpPacket>()

    @Synchronized
    fun receivePacket(tcpPacket: TcpPacket) {
        try {
            packets.add(tcpPacket)
            val printNevermind = packets.size > 20
            handlePackets()
            packets.clear()
            if (printNevermind) {
                println("${getLogPrefix()} : Nevermind, everything worked as planned.")
            }
        } catch (e: IncompleteMessageException) {
            // Nothing
        } catch (e: Exception) {
            if (VldbCoreInitializer.DEBUG && packets.size == 20) {
                println("${getLogPrefix()} : Couldn't read message - ${e.message} (packets count : ${packets.size})")
                println("Packets order        : ${packets.joinToString(", ") { it.header.sequenceNumberAsLong.toString() }}")
                println("Sorted Packets order : ${getSortedPackets().joinToString(", ") { it.header.sequenceNumberAsLong.toString() }}")
                val orderedRawData =
                    getSortedPackets().joinToString("|") { Hex.encodeHexString(it.payload.rawData) }
                val rawData = packets.joinToString("|") { Hex.encodeHexString(it.payload.rawData) }
                println("Packets content         : $rawData")
                println("Ordered packets content : $orderedRawData")
            }
        }
        if (packets.size == 20) {
            println("${getLogPrefix()} : Large packet buffer, character might have crashed. If a character is stuck, please reload sniffer.")
        }
    }

    private fun getLogPrefix(): String =
        "${state.connection.characterName} - ${state.connection.client.ip}:${state.connection.client.port} [${packetOrigin.name}]"

    private fun handlePackets() {
        val rawData = getSortedPackets().flatMap { it.payload.rawData.toList() }.toByteArray()
        parseMessagePremises(ByteArrayReader(rawData)).forEach(this::processMessagePremise)
    }

    private fun getSortedPackets(): List<TcpPacket> {
        return packets.sortedBy { it.header.sequenceNumberAsLong }
    }

    private fun processMessagePremise(messagePremise: DofusMessagePremise) {
        val premiseStr = "[${packetOrigin.name}] ${messagePremise.eventClass.simpleName}:${messagePremise.eventId}"
        val message = deserializeMessage(messagePremise)
        addMessageToStore(message)
        state.logger.log(premiseStr, description = objectMapper.writeValueAsString(message))
    }

    private fun deserializeMessage(messagePremise: DofusMessagePremise): NetworkMessage {
        return try {
            messagePremise.eventClass.getConstructor().newInstance().also { it.deserialize(messagePremise.stream) }
        } catch (t: Throwable) {
            throw ParseFailedException(messagePremise.eventClass.simpleName, messagePremise.eventId, t)
        }
    }

    private fun addMessageToStore(message: NetworkMessage) {
        try {
            state.eventStore.addSocketEvent(message, state.connection)
        } catch (t: Throwable) {
            throw AddToStoreFailedException(message::class.java.toString(), t)
        }
    }

    private fun parseMessagePremises(data: ByteArrayReader): List<DofusMessagePremise> {
        val premises = ArrayList<DofusMessagePremise>()
        while (data.available() > 0) {
            premises.add(parseMessagePremise(data))
        }
        return premises
    }

    private fun parseMessagePremise(src: ByteArrayReader): DofusMessagePremise {
        if (src.available() >= 2) {
            val header = src.readUnsignedShort()
            val lengthType = header and BIT_MASK
            val messageId = header shr BIT_RIGHT_SHIFT_LEN_PACKET_ID
            if (src.available() >= lengthType) {
                val messageLength = readMessageLength(header, lengthType, src)
                if (src.available() >= messageLength) {
                    val stream = ByteArrayReader(src.readNBytes(messageLength))
                    return DofusMessageReceiverUtil.parseMessagePremise(stream, messageId)
                }
            }
        }
        throw IncompleteMessageException()
    }

    protected abstract fun readMessageLength(header: Int, lengthType: Int, src: ByteArrayReader): Int

}