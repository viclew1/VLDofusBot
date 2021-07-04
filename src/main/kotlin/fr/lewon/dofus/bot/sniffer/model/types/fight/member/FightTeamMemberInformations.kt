package fr.lewon.dofus.bot.sniffer.model.types.fight.member

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class FightTeamMemberInformations : INetworkType {

    var id = -1.0

    override fun deserialize(stream: ByteArrayReader) {
        id = stream.readDouble()
    }
}