package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountInformationsForPaddock : NetworkType() {
	var modelId: Int = 0
	var name: String = ""
	var ownerName: String = ""
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		modelId = stream.readVarShort().toInt()
		name = stream.readUTF()
		ownerName = stream.readUTF()
	}
}
