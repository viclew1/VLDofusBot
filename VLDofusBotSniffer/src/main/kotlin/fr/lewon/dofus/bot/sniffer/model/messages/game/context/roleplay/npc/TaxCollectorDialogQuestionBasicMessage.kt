package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.BasicAllianceInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorDialogQuestionBasicMessage : NetworkMessage() {
	lateinit var allianceInfo: BasicAllianceInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceInfo = BasicAllianceInformations()
		allianceInfo.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8033
}
