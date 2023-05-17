package fr.lewon.dofus.bot.core.d2p.elem.graphical.impl

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.d2p.elem.graphical.GraphicalElementData
import fr.lewon.dofus.bot.core.ui.UIPoint

open class NormalGraphicalElementData(elementId: Int, elementType: Int) : GraphicalElementData(elementId, elementType) {

    var gfxId = 0
    var height = 0
    var horizontalSymmetry = false
    var origin = UIPoint()
    var size = UIPoint()

    override fun deserialize(stream: ByteArrayReader, version: Int) {
        gfxId = stream.readInt()
        height = stream.readUnsignedByte()
        horizontalSymmetry = stream.readBoolean()
        origin = UIPoint(stream.readShort().toFloat(), stream.readShort().toFloat())
        size = UIPoint(stream.readShort().toFloat(), stream.readShort().toFloat())
    }

}