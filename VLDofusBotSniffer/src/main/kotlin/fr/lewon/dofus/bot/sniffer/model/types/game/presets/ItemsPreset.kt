package fr.lewon.dofus.bot.sniffer.model.types.game.presets

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ItemsPreset : Preset() {
	var items: ArrayList<ItemForPreset> = ArrayList()
	var mountEquipped: Boolean = false
	lateinit var look: EntityLook
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		items = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ItemForPreset()
			item.deserialize(stream)
			items.add(item)
		}
		mountEquipped = stream.readBoolean()
		look = EntityLook()
		look.deserialize(stream)
	}
}
