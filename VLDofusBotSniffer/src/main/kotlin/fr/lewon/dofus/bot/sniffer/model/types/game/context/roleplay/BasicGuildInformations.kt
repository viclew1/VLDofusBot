package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.social.AbstractSocialGroupInfos
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicGuildInformations : AbstractSocialGroupInfos() {
	var guildId: Int = 0
	var guildName: String = ""
	var guildLevel: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		guildId = stream.readVarInt().toInt()
		guildName = stream.readUTF()
		guildLevel = stream.readUnsignedByte().toInt()
	}
}
