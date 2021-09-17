package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.disposition.IdentifiedEntityDispositionInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameEntitiesDispositionMessage : INetworkMessage {

    var dispositions = ArrayList<IdentifiedEntityDispositionInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        for (i in 0 until stream.readUnsignedShort()) {
            val item = IdentifiedEntityDispositionInformations()
            item.deserialize(stream)
            dispositions.add(item)
        }
    }
}