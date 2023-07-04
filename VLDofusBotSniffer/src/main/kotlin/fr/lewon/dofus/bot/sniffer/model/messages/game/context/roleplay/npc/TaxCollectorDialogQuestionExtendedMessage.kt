package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.BasicAllianceInformations
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.BasicNamedAllianceInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TaxCollectorDialogQuestionExtendedMessage : TaxCollectorDialogQuestionBasicMessage() {
	var maxPods: Int = 0
	var prospecting: Int = 0
	lateinit var alliance: BasicNamedAllianceInformations
	var taxCollectorsCount: Int = 0
	var taxCollectorAttack: Int = 0
	var pods: Int = 0
	var itemsValue: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		maxPods = stream.readVarShort().toInt()
		prospecting = stream.readVarShort().toInt()
		alliance = BasicNamedAllianceInformations()
		alliance.deserialize(stream)
		taxCollectorsCount = stream.readUnsignedByte().toInt()
		taxCollectorAttack = stream.readInt().toInt()
		pods = stream.readVarInt().toInt()
		itemsValue = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 6709
}
