package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.alliance.AllianceMemberInfo
import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.prism.PrismGeolocalizedInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.social.AllianceFactSheetInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceInsiderInfoMessage : NetworkMessage() {
	lateinit var allianceInfos: AllianceFactSheetInformation
	var members: ArrayList<AllianceMemberInfo> = ArrayList()
	var prisms: ArrayList<PrismGeolocalizedInformation> = ArrayList()
	var taxCollectors: ArrayList<TaxCollectorInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceInfos = AllianceFactSheetInformation()
		allianceInfos.deserialize(stream)
		members = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = AllianceMemberInfo()
			item.deserialize(stream)
			members.add(item)
		}
		prisms = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<PrismGeolocalizedInformation>(stream.readUnsignedShort())
			item.deserialize(stream)
			prisms.add(item)
		}
		taxCollectors = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TaxCollectorInformations()
			item.deserialize(stream)
			taxCollectors.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 5093
}
