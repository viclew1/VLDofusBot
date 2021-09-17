package fr.lewon.dofus.bot.sniffer.model.messages.fight

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.messages.INetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightShowFighterMessage : INetworkMessage {

    lateinit var informations: GameFightFighterInformations

    override fun deserialize(stream: ByteArrayReader) {
        informations = TypeManager.getInstance(stream.readUnsignedShort())
        informations.deserialize(stream)
    }
}