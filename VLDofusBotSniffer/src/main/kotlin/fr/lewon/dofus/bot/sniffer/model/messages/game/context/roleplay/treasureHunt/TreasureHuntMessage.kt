package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.treasureHunt

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.TreasureHuntFlag
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.TreasureHuntStep
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class TreasureHuntMessage : NetworkMessage() {
	var questType: Int = 0
	var startMapId: Double = 0.0
	var knownStepsList: ArrayList<TreasureHuntStep> = ArrayList()
	var totalStepCount: Int = 0
	var checkPointCurrent: Int = 0
	var checkPointTotal: Int = 0
	var availableRetryCount: Int = 0
	var flags: ArrayList<TreasureHuntFlag> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		questType = stream.readUnsignedByte().toInt()
		startMapId = stream.readDouble().toDouble()
		knownStepsList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<TreasureHuntStep>(stream.readUnsignedShort())
			item.deserialize(stream)
			knownStepsList.add(item)
		}
		totalStepCount = stream.readUnsignedByte().toInt()
		checkPointCurrent = stream.readVarInt().toInt()
		checkPointTotal = stream.readVarInt().toInt()
		availableRetryCount = stream.readInt().toInt()
		flags = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = TreasureHuntFlag()
			item.deserialize(stream)
			flags.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 2986
}
