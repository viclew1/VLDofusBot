package fr.lewon.dofus.bot.sniffer.model.messages

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayShowActorMessage : INetworkType {

    lateinit var actorInformations: GameRolePlayActorInformations

    override fun deserialize(stream: ByteArrayReader) {
        actorInformations = TypeManager.getInstance(stream.readShort())
        actorInformations.deserialize(stream)
    }

}