package fr.lewon.dofus.bot.sniffer.model.types.actor

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.fight.disposition.EntityDispositionInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class GameContextActorPositionInformations : INetworkType {

    var contextualId: Double = -1.0
    lateinit var disposition: EntityDispositionInformations

    override fun deserialize(stream: ByteArrayReader) {
        contextualId = stream.readDouble()
        disposition = TypeManager.getInstance(stream.readUnsignedShort())
        disposition.deserialize(stream)
    }
}