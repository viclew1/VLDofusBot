package fr.lewon.dofus.bot.sniffer.model.messages.game.context

import fr.lewon.dofus.bot.sniffer.model.types.game.context.ActorOrientation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameMapChangeOrientationMessage : NetworkMessage() {
	lateinit var orientation: ActorOrientation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		orientation = ActorOrientation()
		orientation.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 9176
}
