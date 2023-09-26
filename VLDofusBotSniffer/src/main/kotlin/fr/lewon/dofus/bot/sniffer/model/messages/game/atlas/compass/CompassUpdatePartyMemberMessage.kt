package fr.lewon.dofus.bot.sniffer.model.messages.game.atlas.compass

import fr.lewon.dofus.bot.sniffer.model.types.game.context.MapCoordinates
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CompassUpdatePartyMemberMessage : CompassUpdateMessage() {
	var memberId: Double = 0.0
	var active: Boolean = false
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		memberId = stream.readVarLong().toDouble()
		active = stream.readBoolean()
	}
	override fun getNetworkMessageId(): Int = 7789
}
