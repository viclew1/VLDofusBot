package fr.lewon.dofus.bot.sniffer.model.types.game.paddock

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.ObjectItemInRolePlay
import fr.lewon.dofus.bot.sniffer.model.types.game.mount.ItemDurability
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class PaddockItem : ObjectItemInRolePlay() {
	lateinit var durability: ItemDurability
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		durability = ItemDurability()
		durability.deserialize(stream)
	}
}
