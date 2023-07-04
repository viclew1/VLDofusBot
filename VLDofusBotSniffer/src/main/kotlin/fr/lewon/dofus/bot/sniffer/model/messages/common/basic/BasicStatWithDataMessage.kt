package fr.lewon.dofus.bot.sniffer.model.messages.common.basic

import fr.lewon.dofus.bot.sniffer.model.types.common.basic.StatisticData
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicStatWithDataMessage : BasicStatMessage() {
	var datas: ArrayList<StatisticData> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		datas = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<StatisticData>(stream.readUnsignedShort())
			item.deserialize(stream)
			datas.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2819
}
