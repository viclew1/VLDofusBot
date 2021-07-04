package fr.lewon.dofus.bot.sniffer.model.types.actor

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.types.actor.entity.EntityDispositionInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class GameContextActorPositionInformations : INetworkType {

    var contextualId: Double = -1.0
    lateinit var disposition: EntityDispositionInformations

    override fun deserialize(stream: ByteArrayReader) {
        contextualId = stream.readDouble()
        stream.readShort()
        disposition = EntityDispositionInformations()
        disposition.deserialize(stream)
    }
}