package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class ObjectEffectLadder : ObjectEffectCreature() {

    var monsterCount = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        monsterCount = stream.readVarInt()
    }
}