package fr.lewon.dofus.bot.sniffer.model.types.game.social

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialEmblem : NetworkType() {
	var symbolShape: Int = 0
	var symbolColor: Int = 0
	var backgroundShape: Int = 0
	var backgroundColor: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		symbolShape = stream.readVarShort().toInt()
		symbolColor = stream.readInt().toInt()
		backgroundShape = stream.readUnsignedByte().toInt()
		backgroundColor = stream.readInt().toInt()
	}
}
