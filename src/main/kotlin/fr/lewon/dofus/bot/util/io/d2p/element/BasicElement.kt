package fr.lewon.dofus.bot.util.io.d2p.element

import fr.lewon.dofus.bot.util.io.d2p.cell.Cell
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

abstract class BasicElement(val cell: Cell) {

    abstract fun read(mapVersion: Int, stream: ByteArrayReader)

}