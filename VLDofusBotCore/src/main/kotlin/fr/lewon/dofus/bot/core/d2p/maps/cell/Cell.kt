package fr.lewon.dofus.bot.core.d2p.maps.cell

import fr.lewon.dofus.bot.core.d2p.maps.element.BasicElement
import fr.lewon.dofus.bot.core.d2p.maps.element.ElementType
import fr.lewon.dofus.bot.core.d2p.maps.element.GraphicalElement
import fr.lewon.dofus.bot.core.d2p.maps.element.SoundElement
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class Cell {

    var cellId = 0
    val graphicalElements = ArrayList<GraphicalElement>()

    fun deserialize(stream: ByteArrayReader, mapVersion: Int) {
        cellId = stream.readUnsignedShort()
        val elementsCount = stream.readUnsignedShort()
        for (i in 0 until elementsCount) {
            val be = getBasicElement(stream.readByte().toInt(), this)
            be.deserialize(mapVersion, stream)
            if (be is GraphicalElement) {
                graphicalElements.add(be)
            }
        }
    }

    private fun getBasicElement(typeValue: Int, cell: Cell): BasicElement {
        return when (typeValue) {
            ElementType.GRAPHICAL.typeValue -> GraphicalElement(cell)
            ElementType.SOUND.typeValue -> SoundElement(cell)
            else -> error("Invalid element type : $typeValue")
        }
    }

}