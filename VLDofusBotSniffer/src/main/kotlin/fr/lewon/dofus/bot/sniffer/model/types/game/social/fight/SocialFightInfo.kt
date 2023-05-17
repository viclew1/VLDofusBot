package fr.lewon.dofus.bot.sniffer.model.types.game.social.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialFightInfo : NetworkType() {
	var fightId: Int = 0
	var fightType: Int = 0
	var mapId: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		fightType = stream.readUnsignedByte().toInt()
		mapId = stream.readDouble().toDouble()
	}
}
