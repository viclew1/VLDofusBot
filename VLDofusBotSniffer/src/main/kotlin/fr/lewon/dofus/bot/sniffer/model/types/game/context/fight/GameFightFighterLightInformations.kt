package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightFighterLightInformations : NetworkType() {
	var sex: Boolean = false
	var alive: Boolean = false
	var id: Double = 0.0
	var wave: Int = 0
	var level: Int = 0
	var breed: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		sex = BooleanByteWrapper.getFlag(_box0, 0)
		alive = BooleanByteWrapper.getFlag(_box0, 1)
		id = stream.readDouble().toDouble()
		wave = stream.readUnsignedByte().toInt()
		level = stream.readVarShort().toInt()
		breed = stream.readUnsignedByte().toInt()
	}
}
