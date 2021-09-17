package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GroupMonsterStaticInformationsWithAlternatives : GroupMonsterStaticInformations() {

    var alternatives = ArrayList<AlternativeMonstersInGroupLightInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        for (i in 0 until stream.readUnsignedShort()) {
            val alternative = AlternativeMonstersInGroupLightInformations()
            alternative.deserialize(stream)
            alternatives.add(alternative)
        }
    }
}