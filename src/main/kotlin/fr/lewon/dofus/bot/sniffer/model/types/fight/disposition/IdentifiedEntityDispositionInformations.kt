package fr.lewon.dofus.bot.sniffer.model.types.fight.disposition

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class IdentifiedEntityDispositionInformations : EntityDispositionInformations() {

    var id = 0.0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        id = stream.readDouble()
    }
}