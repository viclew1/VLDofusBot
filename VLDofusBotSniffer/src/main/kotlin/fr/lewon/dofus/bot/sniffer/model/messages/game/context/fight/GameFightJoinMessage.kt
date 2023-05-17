package fr.lewon.dofus.bot.sniffer.model.messages.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightJoinMessage : NetworkMessage() {
	var isTeamPhase: Boolean = false
	var canBeCancelled: Boolean = false
	var canSayReady: Boolean = false
	var isFightStarted: Boolean = false
	var timeMaxBeforeFightStart: Int = 0
	var fightType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		isTeamPhase = BooleanByteWrapper.getFlag(_box0, 0)
		canBeCancelled = BooleanByteWrapper.getFlag(_box0, 1)
		canSayReady = BooleanByteWrapper.getFlag(_box0, 2)
		isFightStarted = BooleanByteWrapper.getFlag(_box0, 3)
		timeMaxBeforeFightStart = stream.readUnsignedShort().toInt()
		fightType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 7476
}
