package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.inventory.UpdatedStorageTabInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildUpdateChestTabRequestMessage : NetworkMessage() {
	lateinit var tab: UpdatedStorageTabInformation
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		tab = UpdatedStorageTabInformation()
		tab.deserialize(stream)
	}
	override fun getNetworkMessageId(): Int = 4124
}
