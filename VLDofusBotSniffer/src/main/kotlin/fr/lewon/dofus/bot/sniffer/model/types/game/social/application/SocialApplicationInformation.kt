package fr.lewon.dofus.bot.sniffer.model.types.game.social.application

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialApplicationInformation : NetworkType() {
	lateinit var playerInfo: ApplicationPlayerInformation
	var applyText: String = ""
	var creationDate: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		playerInfo = ApplicationPlayerInformation()
		playerInfo.deserialize(stream)
		applyText = stream.readUTF()
		creationDate = stream.readDouble().toDouble()
	}
}
