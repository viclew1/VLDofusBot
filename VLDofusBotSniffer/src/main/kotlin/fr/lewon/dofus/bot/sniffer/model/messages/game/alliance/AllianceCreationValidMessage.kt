package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.social.SocialEmblem
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceCreationValidMessage : NetworkMessage() {
	var allianceName: String = ""
	var allianceTag: String = ""
	lateinit var allianceEmblem: SocialEmblem
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceName = stream.readUTF()
		allianceTag = stream.readUTF()
		allianceEmblem = SocialEmblem()
		allianceEmblem.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 8890
}
