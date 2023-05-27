package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.mount.MountClientData
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ExchangeStartOkMountWithOutPaddockMessage : NetworkMessage() {
	var stabledMountsDescription: ArrayList<MountClientData> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		stabledMountsDescription = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = MountClientData()
			item.deserialize(stream)
			stabledMountsDescription.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 7515
}
