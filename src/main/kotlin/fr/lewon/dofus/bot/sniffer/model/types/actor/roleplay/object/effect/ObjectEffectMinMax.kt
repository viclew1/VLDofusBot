package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class ObjectEffectMinMax : ObjectEffect() {

    var min = -1
    var max = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        min = stream.readVarInt()
        max = stream.readVarInt()
    }
}