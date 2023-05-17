package fr.lewon.dofus.bot.sniffer.model.types.game.character.alteration

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffect
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AlterationInfo : NetworkType() {
	var alterationId: Int = 0
	var creationTime: Double = 0.0
	var expirationType: Int = 0
	var expirationValue: Double = 0.0
	var effects: ArrayList<ObjectEffect> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alterationId = stream.readInt().toInt()
		creationTime = stream.readDouble().toDouble()
		expirationType = stream.readUnsignedByte().toInt()
		expirationValue = stream.readDouble().toDouble()
		effects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<ObjectEffect>(stream.readUnsignedShort())
			item.deserialize(stream)
			effects.add(item)
		}
	}
}
