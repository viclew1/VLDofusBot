package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class ObjectEffectInteger : ObjectEffect() {

    var value = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        value = stream.readVarInt()
    }
}