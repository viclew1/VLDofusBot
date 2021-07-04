package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class MonsterInGroupLightInformations : INetworkType {

    var genericId = -1
    var grade = -1
    var level = -1

    override fun deserialize(stream: ByteArrayReader) {
        genericId = stream.readInt()
        grade = stream.readByte().toInt()
        level = stream.readShort()
    }
}