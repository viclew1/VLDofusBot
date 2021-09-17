package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

abstract class AbstractFightTeamInformations : INetworkType {

    var teamId = -1
    var leaderId = -1.0
    var teamSide = -1
    var teamTypeId = -1
    var nbWaves = -1

    override fun deserialize(stream: ByteArrayReader) {
        teamId = stream.readByte().toInt()
        leaderId = stream.readDouble()
        teamSide = stream.readByte().toInt()
        teamTypeId = stream.readByte().toInt()
        nbWaves = stream.readByte().toInt()
    }
}