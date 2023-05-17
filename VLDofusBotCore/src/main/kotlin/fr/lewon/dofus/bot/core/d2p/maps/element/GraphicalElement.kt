package fr.lewon.dofus.bot.core.d2p.maps.element

import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.d2p.maps.cell.Cell
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.ui.UIPoint


class GraphicalElement(cell: Cell) : BasicElement(cell, ElementType.GRAPHICAL.typeValue) {

    var elementId = -1
    var identifier = -1
    val pixelOffset = UIPoint()
    var altitude = 0

    override fun deserialize(mapVersion: Int, stream: ByteArrayReader) {
        elementId = stream.readInt()
        stream.skip(6)
        if (mapVersion <= 4) {
            pixelOffset.x = stream.readUnsignedByte() * D2PMapsAdapter.CELL_HALF_WIDTH
            pixelOffset.y = stream.readUnsignedByte() * D2PMapsAdapter.CELL_HALF_HEIGHT
        } else {
            this.pixelOffset.x = stream.readShort().toFloat()
            this.pixelOffset.y = stream.readShort().toFloat()
        }
        altitude = stream.readUnsignedByte()
        identifier = stream.readInt()
    }

}