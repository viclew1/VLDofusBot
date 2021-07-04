package fr.lewon.dofus.bot.sniffer.model.types.element

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class InteractiveElementSkill : INetworkType {

    var skillId = -1
    var skillInstanceUid = -1

    override fun deserialize(stream: ByteArrayReader) {
        skillId = stream.readVarInt()
        skillInstanceUid = stream.readInt()
    }
}