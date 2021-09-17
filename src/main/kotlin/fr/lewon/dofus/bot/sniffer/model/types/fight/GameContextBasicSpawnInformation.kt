package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.GameContextActorPositionInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameContextBasicSpawnInformation : INetworkType {

    var teamId = 0
    var alive = false
    lateinit var informations: GameContextActorPositionInformations

    override fun deserialize(stream: ByteArrayReader) {
        teamId = stream.readByte().toInt()
        alive = stream.readBoolean()
        informations = TypeManager.getInstance(stream.readUnsignedShort())
        informations.deserialize(stream)
    }

}