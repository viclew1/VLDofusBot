package fr.lewon.dofus.bot.sniffer.model.messages.game.guild.application

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GuildInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildApplicationIsAnsweredMessage : NetworkMessage() {
	var accepted: Boolean = false
	lateinit var guildInformation: GuildInformations
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		accepted = stream.readBoolean()
		guildInformation = GuildInformations()
		guildInformation.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 334
}
