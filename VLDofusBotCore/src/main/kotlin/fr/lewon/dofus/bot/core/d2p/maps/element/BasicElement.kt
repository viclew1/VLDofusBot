package fr.lewon.dofus.bot.core.d2p.maps.element

import fr.lewon.dofus.bot.core.d2p.maps.cell.Cell
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

abstract class BasicElement(val cell: Cell, var elementType: Int) {

    abstract fun deserialize(mapVersion: Int, stream: ByteArrayReader)

}