package fr.lewon.dofus.bot.util.io.d2p.element

import fr.lewon.dofus.bot.util.io.d2p.cell.Cell
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class SoundElement(cell: Cell) : BasicElement(cell) {

    override fun read(mapVersion: Int, stream: ByteArrayReader) {
        stream.readInt()
        stream.readUnsignedShort()
        stream.readInt()
        stream.readInt()
        stream.readUnsignedShort()
        stream.readUnsignedShort()
    }

}