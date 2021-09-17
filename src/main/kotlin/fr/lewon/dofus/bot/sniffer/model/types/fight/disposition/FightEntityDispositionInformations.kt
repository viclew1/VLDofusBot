package fr.lewon.dofus.bot.sniffer.model.types.fight.disposition

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class FightEntityDispositionInformations : EntityDispositionInformations() {

    var carryingCharacterId = 0.0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        carryingCharacterId = stream.readDouble()
    }
}