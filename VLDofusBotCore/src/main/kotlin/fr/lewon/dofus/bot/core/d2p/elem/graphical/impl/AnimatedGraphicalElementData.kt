package fr.lewon.dofus.bot.core.d2p.elem.graphical.impl

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class AnimatedGraphicalElementData(elementId: Int, elementType: Int) :
    NormalGraphicalElementData(elementId, elementType) {

    var minDelay = 0
    var maxDelay = 0

    override fun deserialize(stream: ByteArrayReader, version: Int) {
        super.deserialize(stream, version)
        minDelay = stream.readInt()
        maxDelay = stream.readInt()
    }
}