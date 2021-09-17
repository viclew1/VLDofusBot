package fr.lewon.dofus.bot.sniffer.model.types.hunt

import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.filemanagers.DTBDofusMapManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class TreasureHuntFlag : INetworkType {

    lateinit var map: DofusMap
    var state = -1

    override fun deserialize(stream: ByteArrayReader) {
        val mapId = stream.readDouble()
        map = DTBDofusMapManager.getDofusMap(mapId)
        state = stream.readByte().toInt()
    }
}