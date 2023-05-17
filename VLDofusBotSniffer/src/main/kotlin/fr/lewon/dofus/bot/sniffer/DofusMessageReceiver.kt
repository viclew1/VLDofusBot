package fr.lewon.dofus.bot.sniffer

import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.sniffer.store.EventStore
import org.pcap4j.core.BpfProgram.BpfCompileMode
import org.pcap4j.core.PacketListener
import org.pcap4j.core.PcapHandle
import org.pcap4j.core.PcapNetworkInterface
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.Pcaps
import org.pcap4j.packet.IpV4Packet
import org.pcap4j.packet.Packet
import org.pcap4j.packet.TcpPacket
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock

class DofusMessageReceiver(networkInterfaceName: String) {

    private val lock = ReentrantLock()
    private val connectionByHostPort = HashMap<Host, DofusConnection>()
    private val characterReceiverByConnection = HashMap<DofusConnection, DofusMessageCharacterReceiver>()
    private var sniffer: Sniffer? = null

    init {
        updateSniffer(networkInterfaceName)
    }

    private fun treatEthernetPacket(ethernetPacket: Packet) {
        val ipV4Packet = ethernetPacket.payload
        if (ipV4Packet != null) {
            val tcpPacket = ipV4Packet.payload
            if (tcpPacket != null) {
                if (tcpPacket.payload != null) {
                    val srcPort = (tcpPacket as TcpPacket).header.srcPort.valueAsString()
                    val srcIp = (ipV4Packet as IpV4Packet).header.srcAddr.hostAddress
                    val dstPort = tcpPacket.header.dstPort.valueAsString()
                    val dstIp = ipV4Packet.header.dstAddr.hostAddress
                    val srcHost = Host(srcIp, srcPort)
                    val dstHost = Host(dstIp, dstPort)
                    getCharacterReceiver(dstHost)?.treatReceivedTcpPacket(tcpPacket)
                        ?: getCharacterReceiver(srcHost)?.treatSentTcpPacket(tcpPacket)
                }
            }
        }
    }

    private fun getCharacterReceiver(host: Host): DofusMessageCharacterReceiver? {
        return lock.executeSyncOperation {
            connectionByHostPort[host]?.let { characterReceiverByConnection[it] }
        }
    }

    fun startListening(connection: DofusConnection, eventStore: EventStore, logger: VldbLogger) {
        lock.executeSyncOperation {
            stopListening(connection.client)
            connectionByHostPort[connection.client] = connection
            characterReceiverByConnection[connection] = DofusMessageCharacterReceiver(connection, eventStore, logger)
            sniffer?.updateFilter(connectionByHostPort.values)
        }
    }

    fun stopListening(host: Host) {
        lock.executeSyncOperation {
            val connection = connectionByHostPort.remove(host)
            characterReceiverByConnection.remove(connection)
            sniffer?.updateFilter(connectionByHostPort.values)
        }
    }

    fun updateSniffer(networkInterfaceName: String) {
        println("Restarting Sniffer, network interface : $networkInterfaceName")
        sniffer?.interrupt()
        sniffer?.join()
        connectionByHostPort.keys.forEach(this::stopListening)
        val inetAddress = DofusMessageReceiverUtil.findInetAddress(networkInterfaceName)
            ?: error("No active address found. Make sure you have an internet connection.")
        val nif = Pcaps.getDevByAddress(inetAddress)
            ?: error("No active device found. Make sure Npcap is installed.")
        sniffer = Sniffer(nif, this::treatEthernetPacket).also { it.start() }
    }

    private class Sniffer(nif: PcapNetworkInterface, val treatPacket: (Packet) -> Unit) : Thread() {

        private val handle: PcapHandle
        private val packetListener: PacketListener
        private val ethernetPackets = ConcurrentLinkedQueue<Packet>()
        private val treatPacketsThread: Thread

        init {
            handle = PcapHandle.Builder(nif.name)
                .snaplen(65536000)
                .promiscuousMode(PromiscuousMode.PROMISCUOUS)
                .timeoutMillis(10000)
                .bufferSize(20 * 1024 * 1024)
                .build()
            handle.blockingMode = PcapHandle.BlockingMode.NONBLOCKING
            updateFilter(emptyList())
            packetListener = PacketListener {
                ethernetPackets.add(it)
            }
            treatPacketsThread = Thread {
                try {
                    while (true) {
                        if (ethernetPackets.isNotEmpty()) {
                            treatPacket(ethernetPackets.poll())
                        } else {
                            sleep(100)
                        }
                    }
                } catch (e: Throwable) {
                    println("Sniffer treatment thread stopped - ${e.message}")
                }
            }
        }

        fun updateFilter(connections: Collection<DofusConnection>) {
            handle.setFilter(buildFilter(connections), BpfCompileMode.OPTIMIZE)
        }

        private fun buildFilter(connections: Collection<DofusConnection>): String {
            return if (connections.isEmpty()) {
                "src host 255.255.255.255 and dst host 255.255.255.255"
            } else {
                val clientToServerFilters = connections.map {
                    "src host ${it.client.ip} and src port ${it.client.port} and dst host ${it.server.ip} and dst port ${it.server.port}"
                }
                val serverToClientFilters = connections.map {
                    "src host ${it.server.ip} and src port ${it.server.port} and dst host ${it.client.ip} and dst port ${it.client.port}"
                }
                clientToServerFilters.plus(serverToClientFilters).joinToString(" or ") { "($it)" }
            }
        }

        override fun run() {
            try {
                println("Sniffer started.")
                treatPacketsThread.start()
                handle.loop(-1, packetListener)
            } catch (e: Exception) {
                println("Sniffer stopped - ${e.message}")
            }
        }

        override fun interrupt() {
            treatPacketsThread.interrupt()
            treatPacketsThread.join()
            handle.breakLoop()
            handle.close()
        }

    }

}
