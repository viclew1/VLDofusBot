package fr.lewon.dofus.bot.sniffer.model.types.element

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class InteractiveElementWithAgeBonus : InteractiveElement() {

    var ageBonus = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        ageBonus = stream.readUnsignedShort()
    }
}