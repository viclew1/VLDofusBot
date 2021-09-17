package fr.lewon.dofus.bot.util.io.d2p.element

import fr.lewon.dofus.bot.util.io.d2p.cell.Cell
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader


class GraphicalElement(cell: Cell) : BasicElement(cell) {

    override fun read(mapVersion: Int, stream: ByteArrayReader) {
        stream.readInt()
        stream.readByte()
        stream.readByte()
        stream.readByte()
        stream.readByte()
        stream.readByte()
        stream.readByte()
        if (mapVersion <= 4) {
            stream.readByte()
            stream.readByte()
        } else {
            stream.readUnsignedShort()
            stream.readUnsignedShort()
        }
        stream.readByte()
        stream.readInt()
    }

}