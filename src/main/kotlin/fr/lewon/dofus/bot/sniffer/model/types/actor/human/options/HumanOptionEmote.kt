package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class HumanOptionEmote : HumanOption() {

    var emoteId = -1
    var emoteStartType = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        emoteId = stream.readByte().toUByte().toInt()
        emoteStartType = stream.readDouble()
    }
}