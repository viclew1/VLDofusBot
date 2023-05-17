package fr.lewon.dofus.bot.core.d2p.maps.element

import fr.lewon.dofus.bot.core.d2p.maps.cell.Cell
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class SoundElement(cell: Cell) : BasicElement(cell, ElementType.SOUND.typeValue) {

    override fun deserialize(mapVersion: Int, stream: ByteArrayReader) {
        stream.readInt()
        stream.readUnsignedShort()
        stream.readInt()
        stream.readInt()
        stream.readUnsignedShort()
        stream.readUnsignedShort()
    }

}