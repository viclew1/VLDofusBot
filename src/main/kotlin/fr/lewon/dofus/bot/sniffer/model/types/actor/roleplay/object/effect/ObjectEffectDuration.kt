package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class ObjectEffectDuration : ObjectEffect() {

    var days = -1
    var hours = -1
    var minutes = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        days = stream.readVarShort()
        hours = stream.readByte().toInt()
        minutes = stream.readByte().toInt()
    }
}