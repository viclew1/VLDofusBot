package fr.lewon.dofus.bot.sniffer.model.messages

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.types.transport.TeleportDestination
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager

class ZaapDestinationsMessage : INetworkType {

    var type = -1
    var destinations = ArrayList<TeleportDestination>()
    lateinit var spawnMap: DofusMap

    override fun deserialize(stream: ByteArrayReader) {
        type = stream.readByte().toInt()
        for (i in 0 until stream.readShort()) {
            val destination = TeleportDestination()
            destination.deserialize(stream)
            destinations.add(destination)
        }
        val spawnMapId = stream.readDouble()
        spawnMap = DTBDofusMapManager.getDofusMap(spawnMapId)
    }

}