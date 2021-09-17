package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameActionFightDeathMessage : AbstractGameActionMessage() {

    var targetId = 0.0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        targetId = stream.readDouble()
    }
}