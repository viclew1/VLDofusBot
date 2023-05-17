package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameFightResumeSlaveInfo : NetworkType() {
	var slaveId: Double = 0.0
	var spellCooldowns: ArrayList<GameFightSpellCooldown> = ArrayList()
	var summonCount: Int = 0
	var bombCount: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		slaveId = stream.readDouble().toDouble()
		spellCooldowns = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = GameFightSpellCooldown()
			item.deserialize(stream)
			spellCooldowns.add(item)
		}
		summonCount = stream.readUnsignedByte().toInt()
		bombCount = stream.readUnsignedByte().toInt()
	}
}
