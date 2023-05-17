package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.alliance.KohAllianceInfo
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KohUpdateMessage : NetworkMessage() {
	var kohAllianceInfo: ArrayList<KohAllianceInfo> = ArrayList()
	var startingAvaTimestamp: Int = 0
	var nextTickTime: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		kohAllianceInfo = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = KohAllianceInfo()
			item.deserialize(stream)
			kohAllianceInfo.add(item)
		}
		startingAvaTimestamp = stream.readInt().toInt()
		nextTickTime = stream.readDouble().toDouble()
	}
	override fun getNetworkMessageId(): Int = 2805
}
