package fr.lewon.dofus.bot.sniffer.model.types.game.friend

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.character.status.PlayerStatus
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AcquaintanceOnlineInformation : AcquaintanceInformation() {
	var playerId: Double = 0.0
	var playerName: String = ""
	var moodSmileyId: Int = 0
	lateinit var status: PlayerStatus
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerId = stream.readVarLong().toDouble()
		playerName = stream.readUTF()
		moodSmileyId = stream.readVarShort().toInt()
		status = ProtocolTypeManager.getInstance<PlayerStatus>(stream.readUnsignedShort())
		status.deserialize(stream)
	}
}
