package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightResultListEntry : NetworkType() {
	var outcome: Int = 0
	var wave: Int = 0
	lateinit var rewards: FightLoot
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		outcome = stream.readVarShort().toInt()
		wave = stream.readUnsignedByte().toInt()
		rewards = FightLoot()
		rewards.deserialize(stream)
	}
}
