package fr.lewon.dofus.bot.sniffer.model.types.transport

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager

class TeleportDestination : INetworkType {

    var type = -1
    lateinit var map: DofusMap
    var subAreaId = -1
    var level = -1
    var cost = -1

    override fun deserialize(stream: ByteArrayReader) {
        type = stream.readByte().toInt()
        val mapId = stream.readDouble()
        map = DTBDofusMapManager.getDofusMap(mapId)
        subAreaId = stream.readVarShort()
        level = stream.readVarShort()
        cost = stream.readVarShort()
    }
}