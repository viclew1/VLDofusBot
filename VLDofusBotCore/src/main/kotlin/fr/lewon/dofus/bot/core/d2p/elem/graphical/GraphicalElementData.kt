package fr.lewon.dofus.bot.core.d2p.elem.graphical

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

abstract class GraphicalElementData(val elementId: Int, val elementType: Int) {

    abstract fun deserialize(stream: ByteArrayReader, version: Int)

}