package fr.lewon.dofus.bot.sniffer.model.types.element

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class StatedElement : INetworkType {

    var elementId = -1
    var elementCellId = -1
    var elementState = -1
    var onCurrentMap = false

    override fun deserialize(stream: ByteArrayReader) {
        elementId = stream.readInt()
        elementCellId = stream.readVarShort()
        elementState = stream.readVarInt()
        onCurrentMap = stream.readBoolean()
    }
}