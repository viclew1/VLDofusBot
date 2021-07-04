package fr.lewon.dofus.bot.sniffer.model.types.house

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.util.BooleanByteWrapper
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class HouseInstanceInformations : INetworkType {

    var instanceId = -1
    var secondHand = false
    var isLocked = false
    var hasOwner = false
    lateinit var ownerName: String
    lateinit var ownerTag: String
    var price = -1L
    var isSaleLocked = false

    override fun deserialize(stream: ByteArrayReader) {
        val box = stream.readByte()
        secondHand = BooleanByteWrapper.getFlag(box, 0)
        isLocked = BooleanByteWrapper.getFlag(box, 1)
        hasOwner = BooleanByteWrapper.getFlag(box, 2)
        isSaleLocked = BooleanByteWrapper.getFlag(box, 3)
        instanceId = stream.readInt()
        ownerName = stream.readUTF()
        ownerTag = stream.readUTF()
        price = stream.readVarLong()
    }
}