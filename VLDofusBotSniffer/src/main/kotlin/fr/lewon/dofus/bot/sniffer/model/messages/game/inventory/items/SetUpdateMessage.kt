package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.items

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffect
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SetUpdateMessage : NetworkMessage() {
	var setId: Int = 0
	var setObjects: ArrayList<Int> = ArrayList()
	var setEffects: ArrayList<ObjectEffect> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		setId = stream.readVarShort().toInt()
		setObjects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			setObjects.add(item)
		}
		setEffects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<ObjectEffect>(stream.readUnsignedShort())
			item.deserialize(stream)
			setEffects.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8372
}
