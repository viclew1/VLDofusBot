package fr.lewon.dofus.bot.sniffer.model.messages.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.game.friend.AcquaintanceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AcquaintancesListMessage : NetworkMessage() {
	var acquaintanceList: ArrayList<AcquaintanceInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		acquaintanceList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<AcquaintanceInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			acquaintanceList.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 9702
}
