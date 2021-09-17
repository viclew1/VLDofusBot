package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameActionFightSlideMessage : AbstractGameActionMessage() {

    var targetId = 0.0
    var startCellId = 0
    var endCellId = 0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        targetId = stream.readDouble()
        startCellId = stream.readUnsignedShort()
        endCellId = stream.readUnsignedShort()
    }
}