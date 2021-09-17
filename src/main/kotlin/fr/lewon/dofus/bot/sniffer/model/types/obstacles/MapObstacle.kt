package fr.lewon.dofus.bot.sniffer.model.types.obstacles

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class MapObstacle : INetworkType {

    var obstacleCellId = -1
    var state = -1

    override fun deserialize(stream: ByteArrayReader) {
        obstacleCellId = stream.readVarShort()
        state = stream.readByte().toInt()
    }
}