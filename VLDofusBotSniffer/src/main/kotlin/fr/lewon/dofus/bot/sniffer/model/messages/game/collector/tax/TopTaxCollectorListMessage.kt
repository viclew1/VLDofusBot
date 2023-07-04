package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TopTaxCollectorListMessage : NetworkMessage() {
	var dungeonTaxCollectorsInformation: ArrayList<TaxCollectorInformations> = ArrayList()
	var worldTaxCollectorsInformation: ArrayList<TaxCollectorInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		dungeonTaxCollectorsInformation = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<TaxCollectorInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			dungeonTaxCollectorsInformation.add(item)
		}
		worldTaxCollectorsInformation = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<TaxCollectorInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			worldTaxCollectorsInformation.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2349
}
