package fr.lewon.dofus.bot.sniffer.model.types.house

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class HouseInformations : INetworkType {

    var houseId = -1
    var modelId = -1

    override fun deserialize(stream: ByteArrayReader) {
        houseId = stream.readVarInt()
        modelId = stream.readVarShort()
    }

}