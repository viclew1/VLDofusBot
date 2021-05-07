package fr.lewon.dofus.bot.sniffer

import org.pcap4j.core.PcapNetworkInterface
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode
import org.pcap4j.core.Pcaps
import org.pcap4j.packet.IpV4Packet
import org.pcap4j.packet.Packet
import java.net.InetAddress


object DofusSniffer {

    fun sniff() {
        val nif = getNetworkInterface()
        println(nif)
        val snapLen = 65536
        val mode = PromiscuousMode.PROMISCUOUS
        val timeout = 10
        val handle = nif.openLive(snapLen, mode, timeout)

        for (i in 0..10) {
            println("------")
            val packet: Packet = handle.nextPacketEx
            val ipV4Packet = packet.get(IpV4Packet::class.java)
            val srcAddr = ipV4Packet.header.srcAddr
            println(srcAddr)
            println("from : $srcAddr")
            ipV4Packet.header
            println("content : " + String(ipV4Packet.rawData))
        }

        handle.close()

    }

    private fun getNetworkInterface(): PcapNetworkInterface {
        val addr = InetAddress.getByName("192.168.1.91")
        return Pcaps.getDevByAddress(addr)
    }

}

fun main() {
    DofusSniffer.sniff()
}