package fr.lewon.dofus.bot.core.d2p.maps.cell

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.ui.UIPoint

class Fixture {

    var fixtureId = 0
    var offset = UIPoint()
    var rotation = 0
    var xScale = 0
    var yScale = 0
    var redMultiplier = 0
    var greenMultiplier = 0
    var blueMultiplier = 0
    var alpha = 0

    fun deserialize(stream: ByteArrayReader) {
        fixtureId = stream.readInt()
        offset = UIPoint(stream.readUnsignedShort().toFloat(), stream.readUnsignedShort().toFloat())
        rotation = stream.readUnsignedShort()
        xScale = stream.readUnsignedShort()
        yScale = stream.readUnsignedShort()
        redMultiplier = stream.readUnsignedByte()
        greenMultiplier = stream.readUnsignedByte()
        blueMultiplier = stream.readUnsignedByte()
        alpha = stream.readUnsignedByte()
    }

}