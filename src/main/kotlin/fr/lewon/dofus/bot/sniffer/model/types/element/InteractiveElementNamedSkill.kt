package fr.lewon.dofus.bot.sniffer.model.types.element

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class InteractiveElementNamedSkill : InteractiveElementSkill() {

    var nameId = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        nameId = stream.readVarInt()
    }
}