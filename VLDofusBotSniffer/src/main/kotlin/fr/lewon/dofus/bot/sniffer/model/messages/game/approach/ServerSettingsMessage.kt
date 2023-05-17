package fr.lewon.dofus.bot.sniffer.model.messages.game.approach

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ServerSettingsMessage : NetworkMessage() {
	var isMonoAccount: Boolean = false
	var hasFreeAutopilot: Boolean = false
	var lang: String = ""
	var community: Int = 0
	var gameType: Int = 0
	var arenaLeaveBanTime: Int = 0
	var itemMaxLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		isMonoAccount = BooleanByteWrapper.getFlag(_box0, 0)
		hasFreeAutopilot = BooleanByteWrapper.getFlag(_box0, 1)
		lang = stream.readUTF()
		community = stream.readUnsignedByte().toInt()
		gameType = stream.readUnsignedByte().toInt()
		arenaLeaveBanTime = stream.readVarShort().toInt()
		itemMaxLevel = stream.readInt().toInt()
	}
	override fun getNetworkMessageId(): Int = 8027
}
