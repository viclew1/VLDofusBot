package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.`object`.effect

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class ObjectEffect : INetworkType {

    var actionId = -1

    override fun deserialize(stream: ByteArrayReader) {
        actionId = stream.readVarShort()
    }
}