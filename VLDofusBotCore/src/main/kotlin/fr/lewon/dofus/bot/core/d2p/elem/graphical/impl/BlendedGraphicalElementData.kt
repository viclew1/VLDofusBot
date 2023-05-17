package fr.lewon.dofus.bot.core.d2p.elem.graphical.impl

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader

class BlendedGraphicalElementData(elementId: Int, elementType: Int) :
    NormalGraphicalElementData(elementId, elementType) {

    var blendMode = ""

    override fun deserialize(stream: ByteArrayReader, version: Int) {
        super.deserialize(stream, version)
        blendMode = stream.readString(stream.readInt())
    }
}