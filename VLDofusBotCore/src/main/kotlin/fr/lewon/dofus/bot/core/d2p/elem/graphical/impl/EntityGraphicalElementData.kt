package fr.lewon.dofus.bot.core.d2p.elem.graphical.impl

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.d2p.elem.graphical.GraphicalElementData

class EntityGraphicalElementData(elementId: Int, elementType: Int) : GraphicalElementData(elementId, elementType) {

    var entityLook = ""
    var horizontalSymmetry = false
    var playAnimation = false
    var playAnimStatic = false
    var minDelay = 0
    var maxDelay = 0

    override fun deserialize(stream: ByteArrayReader, version: Int) {
        this.entityLook = stream.readString(stream.readInt())
        this.horizontalSymmetry = stream.readBoolean()
        if (version >= 7) {
            this.playAnimation = stream.readBoolean()
        }
        if (version >= 6) {
            this.playAnimStatic = stream.readBoolean()
        }
        if (version >= 5) {
            this.minDelay = stream.readInt()
            this.maxDelay = stream.readInt()
        }
    }

}