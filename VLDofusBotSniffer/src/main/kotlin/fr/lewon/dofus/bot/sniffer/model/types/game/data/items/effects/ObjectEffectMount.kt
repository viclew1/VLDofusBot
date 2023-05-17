package fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class ObjectEffectMount : ObjectEffect() {
	var sex: Boolean = false
	var isRideable: Boolean = false
	var isFeconded: Boolean = false
	var isFecondationReady: Boolean = false
	var id: Double = 0.0
	var expirationDate: Double = 0.0
	var model: Int = 0
	var name: String = ""
	var owner: String = ""
	var level: Int = 0
	var reproductionCount: Int = 0
	var reproductionCountMax: Int = 0
	var effects: ArrayList<ObjectEffectInteger> = ArrayList()
	var capacities: ArrayList<Int> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		sex = BooleanByteWrapper.getFlag(_box0, 0)
		isRideable = BooleanByteWrapper.getFlag(_box0, 1)
		isFeconded = BooleanByteWrapper.getFlag(_box0, 2)
		isFecondationReady = BooleanByteWrapper.getFlag(_box0, 3)
		id = stream.readVarLong().toDouble()
		expirationDate = stream.readVarLong().toDouble()
		model = stream.readVarInt().toInt()
		name = stream.readUTF()
		owner = stream.readUTF()
		level = stream.readUnsignedByte().toInt()
		reproductionCount = stream.readVarInt().toInt()
		reproductionCountMax = stream.readVarInt().toInt()
		effects = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectEffectInteger()
			item.deserialize(stream)
			effects.add(item)
		}
		capacities = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			capacities.add(item)
		}
	}
}
