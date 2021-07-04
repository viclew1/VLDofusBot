package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.prism

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class PrismInformation : INetworkType {

    var typeId = -1
    var state = -1
    var nextVulnerabilityDate = -1
    var placementDate = -1
    var rewardTokenCount = -1

    override fun deserialize(stream: ByteArrayReader) {
        typeId = stream.readByte().toInt()
        state = stream.readByte().toInt()
        nextVulnerabilityDate = stream.readInt()
        placementDate = stream.readInt()
        rewardTokenCount = stream.readVarInt()
    }
}