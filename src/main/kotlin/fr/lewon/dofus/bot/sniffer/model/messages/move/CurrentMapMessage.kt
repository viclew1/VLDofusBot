package fr.lewon.dofus.bot.sniffer.model.messages.move

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.util.filemanagers.DTBFightCellManager
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class CurrentMapMessage : INetworkMessage {

    var mapId = 0.0
    var mapKey = ""

    override fun deserialize(stream: ByteArrayReader) {
        mapId = stream.readDouble()
        mapKey = stream.readUTF()
        DTBFightCellManager.updateFightBoard(mapId, mapKey)
    }
}