package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import fr.lewon.dofus.bot.util.WindowsUtil
import org.pcap4j.core.BpfProgram.BpfCompileMode
import org.pcap4j.core.PacketListener
import org.pcap4j.core.PcapHandle
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode

object SocketListener : Thread() {

    private const val SOCKET_LENGTH_MASK = 0x03

    private val handle: PcapHandle
    private val packetListener: PacketListener
    private var bufferedPayload: ByteArray = ByteArray(0)

    init {
        val nif = PcapsFinder.findActiveDevice()
        handle = nif.openLive(65536, PromiscuousMode.PROMISCUOUS, -1)
        packetListener = PacketListener { packet ->
            val ethernetPacket = packet.payload
            if (ethernetPacket != null) {
                val tcpPacket = ethernetPacket.payload
                if (tcpPacket != null) {
                    val dofusPacket = tcpPacket.payload
                    if (dofusPacket != null) {
                        parseDofus(bufferedPayload + dofusPacket.rawData)
                    }
                }
            }
        }
    }

    override fun interrupt() {
        handle.breakLoop()
        handle.close()
    }

    override fun run() {
        val serverIp = WindowsUtil.findServerIp()
        handle.setFilter("host $serverIp", BpfCompileMode.OPTIMIZE)
        handle.loop(-1, packetListener)
    }

    private fun parseDofus(data: ByteArray) {
        val socketType = DofusSocketType.fromSocket(data)
        if (data.size >= 4 && socketType != null) {
            val socketLengthSize = data[1].toInt() and SOCKET_LENGTH_MASK
            val prefixLength = 2 + socketLengthSize
            val dataLength = getLength(data, socketLengthSize)
            val expectedTotalLength = prefixLength + dataLength
            val realTotalLength = data.size
            if (realTotalLength < expectedTotalLength) {
                bufferedPayload = data
                return
            }
            bufferedPayload = ByteArray(0)
            val payload = data.copyOfRange(prefixLength, expectedTotalLength)
            readEvent(payload, socketType)
        }
    }

    private fun readEvent(payload: ByteArray, socketType: DofusSocketType) {
        val event = socketType.eventBuilder.invoke()
        event.deserialize(ByteArrayReader(payload))
        EventStore.addSocketEvent(event)
    }

    private fun getLength(data: ByteArray, socketLengthSize: Int): Int {
        val analyzer = ByteArrayReader(data)
        analyzer.skip(2)
        return when (socketLengthSize) {
            0 -> 0
            1 -> analyzer.readByte().toUByte().toInt()
            2 -> analyzer.readShort()
            4 -> analyzer.readInt()
            else -> -1
        }
    }

}