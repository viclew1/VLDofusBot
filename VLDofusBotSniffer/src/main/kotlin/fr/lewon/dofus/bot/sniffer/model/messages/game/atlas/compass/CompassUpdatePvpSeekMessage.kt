package fr.lewon.dofus.bot.sniffer.model.messages.game.atlas.compass

import fr.lewon.dofus.bot.sniffer.model.types.game.context.MapCoordinates
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class CompassUpdatePvpSeekMessage : CompassUpdateMessage() {
	var memberId: Double = 0.0
	var memberName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		memberId = stream.readVarLong().toDouble()
		memberName = stream.readUTF()
	}
	override fun getNetworkMessageId(): Int = 4641
}
