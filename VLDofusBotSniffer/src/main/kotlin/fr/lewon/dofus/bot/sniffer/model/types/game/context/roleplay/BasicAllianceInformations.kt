package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.social.AbstractSocialGroupInfos
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicAllianceInformations : AbstractSocialGroupInfos() {
	var allianceId: Int = 0
	var allianceTag: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceId = stream.readVarInt().toInt()
		allianceTag = stream.readUTF()
	}
}
