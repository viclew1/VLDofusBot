package fr.lewon.dofus.bot.sniffer.model.types.game.mount

import fr.lewon.dofus.bot.sniffer.model.types.game.data.items.effects.ObjectEffectInteger
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class MountClientData : NetworkType() {
	var sex: Boolean = false
	var isRideable: Boolean = false
	var isWild: Boolean = false
	var isFecondationReady: Boolean = false
	var useHarnessColors: Boolean = false
	var id: Double = 0.0
	var model: Int = 0
	var ancestor: ArrayList<Int> = ArrayList()
	var behaviors: ArrayList<Int> = ArrayList()
	var name: String = ""
	var ownerId: Int = 0
	var experience: Double = 0.0
	var experienceForLevel: Double = 0.0
	var experienceForNextLevel: Double = 0.0
	var level: Int = 0
	var maxPods: Int = 0
	var stamina: Int = 0
	var staminaMax: Int = 0
	var maturity: Int = 0
	var maturityForAdult: Int = 0
	var energy: Int = 0
	var energyMax: Int = 0
	var serenity: Int = 0
	var aggressivityMax: Int = 0
	var serenityMax: Int = 0
	var love: Int = 0
	var loveMax: Int = 0
	var fecondationTime: Int = 0
	var boostLimiter: Int = 0
	var boostMax: Double = 0.0
	var reproductionCount: Int = 0
	var reproductionCountMax: Int = 0
	var harnessGID: Int = 0
	var effectList: ArrayList<ObjectEffectInteger> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		sex = BooleanByteWrapper.getFlag(_box0, 0)
		isRideable = BooleanByteWrapper.getFlag(_box0, 1)
		isWild = BooleanByteWrapper.getFlag(_box0, 2)
		isFecondationReady = BooleanByteWrapper.getFlag(_box0, 3)
		useHarnessColors = BooleanByteWrapper.getFlag(_box0, 4)
		id = stream.readDouble().toDouble()
		model = stream.readVarInt().toInt()
		ancestor = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			ancestor.add(item)
		}
		behaviors = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readInt().toInt()
			behaviors.add(item)
		}
		name = stream.readUTF()
		ownerId = stream.readInt().toInt()
		experience = stream.readVarLong().toDouble()
		experienceForLevel = stream.readVarLong().toDouble()
		experienceForNextLevel = stream.readDouble().toDouble()
		level = stream.readUnsignedByte().toInt()
		maxPods = stream.readVarInt().toInt()
		stamina = stream.readVarInt().toInt()
		staminaMax = stream.readVarInt().toInt()
		maturity = stream.readVarInt().toInt()
		maturityForAdult = stream.readVarInt().toInt()
		energy = stream.readVarInt().toInt()
		energyMax = stream.readVarInt().toInt()
		serenity = stream.readInt().toInt()
		aggressivityMax = stream.readInt().toInt()
		serenityMax = stream.readVarInt().toInt()
		love = stream.readVarInt().toInt()
		loveMax = stream.readVarInt().toInt()
		fecondationTime = stream.readInt().toInt()
		boostLimiter = stream.readInt().toInt()
		boostMax = stream.readDouble().toDouble()
		reproductionCount = stream.readInt().toInt()
		reproductionCountMax = stream.readVarInt().toInt()
		harnessGID = stream.readVarInt().toInt()
		effectList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ObjectEffectInteger()
			item.deserialize(stream)
			effectList.add(item)
		}
	}
}
