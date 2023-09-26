package fr.lewon.dofus.bot.sniffer.model.messages.game.collector.tax

import fr.lewon.dofus.bot.sniffer.model.types.game.collector.tax.TaxCollectorBasicInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.BasicAllianceInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorAttackedResultMessage : NetworkMessage() {
	var deadOrAlive: Boolean = false
	lateinit var basicInfos: TaxCollectorBasicInformations
	lateinit var alliance: BasicAllianceInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		deadOrAlive = stream.readBoolean()
		basicInfos = TaxCollectorBasicInformations()
		basicInfos.deserialize(stream)
		alliance = BasicAllianceInformations()
		alliance.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 5228
}
