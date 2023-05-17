package fr.lewon.dofus.bot.sniffer.model.types.game.startup

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.ObjectItemInformationWithQuantity
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameActionItem : NetworkType() {
	var uid: Int = 0
	var title: String = ""
	var text: String = ""
	var descUrl: String = ""
	var pictureUrl: String = ""
	var items: ArrayList<ObjectItemInformationWithQuantity> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uid = stream.readInt().toInt()
		title = stream.readUTF()
		text = stream.readUTF()
		descUrl = stream.readUTF()
		pictureUrl = stream.readUTF()
		items = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectItemInformationWithQuantity()
			item.deserialize(stream)
			items.add(item)
		}
	}
}
