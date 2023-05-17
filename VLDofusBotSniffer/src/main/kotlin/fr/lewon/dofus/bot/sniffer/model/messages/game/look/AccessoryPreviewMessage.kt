package fr.lewon.dofus.bot.sniffer.model.messages.game.look

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AccessoryPreviewMessage : NetworkMessage() {
	lateinit var look: EntityLook
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		look = EntityLook()
		look.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 879
}
