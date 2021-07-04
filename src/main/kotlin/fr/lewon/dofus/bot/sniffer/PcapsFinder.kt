package fr.lewon.dofus.bot.sniffer

import org.pcap4j.core.PcapNetworkInterface
import org.pcap4j.core.Pcaps
import java.net.InetAddress
import java.net.NetworkInterface


object PcapsFinder {

    /** Find the current active pcap network interface.
     * @return The active pcap network interface
     */
    fun findActiveDevice(): PcapNetworkInterface {
        var currentAddress: InetAddress? = null
        val nis = NetworkInterface.getNetworkInterfaces()
        while (nis.hasMoreElements() && currentAddress == null) {
            val ni = nis.nextElement()
            if (ni.isUp && !ni.isLoopback) {
                val ias = ni.inetAddresses
                while (ias.hasMoreElements() && currentAddress == null) {
                    val ia = ias.nextElement()
                    if (ia.isSiteLocalAddress && !ia.isLoopbackAddress) currentAddress = ia
                }
            }
        }
        if (currentAddress == null) error("No active address found. Make sure you have an internet connection.")
        return Pcaps.getDevByAddress(currentAddress)
            ?: error("No active device found. Make sure WinPcap or libpcap is installed.")
    }

}