package fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HumanOptionAlliance : HumanOption() {
	lateinit var allianceInformation: AllianceInformation
	var aggressable: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		allianceInformation = AllianceInformation()
		allianceInformation.deserialize(stream)
		aggressable = stream.readUnsignedByte().toInt()
	}
}
