package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.util.io.stream.BooleanByteWrapper
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class FightOptionsInformations : INetworkType {

    var isSecret = false
    var isRestrictedToPartyOnly = false
    var isClosed = false
    var isAskingForHelp = false

    override fun deserialize(stream: ByteArrayReader) {
        val box = stream.readByte()
        isSecret = BooleanByteWrapper.getFlag(box, 0)
        isRestrictedToPartyOnly = BooleanByteWrapper.getFlag(box, 1)
        isClosed = BooleanByteWrapper.getFlag(box, 2)
        isAskingForHelp = BooleanByteWrapper.getFlag(box, 3)
    }
}