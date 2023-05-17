package fr.lewon.dofus.bot.core.d2p.elem.graphical.impl

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.d2p.elem.graphical.GraphicalElementData

class ParticlesGraphicalElementData(elementId: Int, elementType: Int) : GraphicalElementData(elementId, elementType) {

    var scriptId = 0

    override fun deserialize(stream: ByteArrayReader, version: Int) {
        scriptId = stream.readInt()
    }
}