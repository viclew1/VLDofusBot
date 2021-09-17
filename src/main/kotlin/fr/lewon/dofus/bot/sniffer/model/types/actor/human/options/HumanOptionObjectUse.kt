package fr.lewon.dofus.bot.sniffer.model.types.actor.human.options

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class HumanOptionObjectUse : HumanOption() {

    var delayTypeId = -1
    var delayEndTime = -1.0
    var objectGid = -1

    override fun deserialize(stream: ByteArrayReader) {
        delayTypeId = stream.readByte().toInt()
        delayEndTime = stream.readDouble()
        objectGid = stream.readVarShort()
    }
}