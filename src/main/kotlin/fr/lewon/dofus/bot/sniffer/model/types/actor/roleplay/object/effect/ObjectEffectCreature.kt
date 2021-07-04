package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class ObjectEffectCreature : ObjectEffect() {

    var monsterFamilyId = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        monsterFamilyId = stream.readVarShort()
    }
}