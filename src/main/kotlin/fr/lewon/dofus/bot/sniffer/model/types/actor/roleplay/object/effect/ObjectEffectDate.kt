package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class ObjectEffectDate : ObjectEffect() {

    var year = -1
    var month = -1
    var day = -1
    var hour = -1
    var minute = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        year = stream.readVarShort()
        month = stream.readByte().toInt()
        day = stream.readByte().toInt()
        hour = stream.readByte().toInt()
        minute = stream.readByte().toInt()
    }
}