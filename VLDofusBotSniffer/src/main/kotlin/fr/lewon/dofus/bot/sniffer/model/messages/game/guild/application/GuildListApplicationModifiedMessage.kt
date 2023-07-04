package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.sniffer.model.types.game.social.application.SocialApplicationInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildListApplicationModifiedMessage : NetworkMessage() {
	lateinit var apply: SocialApplicationInformation
	var state: Int = 0
	var playerId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		apply = SocialApplicationInformation()
		apply.deserialize(stream)
		state = stream.readUnsignedByte().toInt()
		playerId = stream.readVarLong().toDouble()
	}
	override fun getNetworkMessageId(): Int = 2316
}
