package fr.lewon.dofus.bot.sniffer.model.types.game.actions.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AbstractFightDispellableEffect : NetworkType() {
	var uid: Int = 0
	var targetId: Double = 0.0
	var turnDuration: Int = 0
	var dispelable: Int = 0
	var spellId: Int = 0
	var effectId: Int = 0
	var parentBoostUid: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		uid = stream.readVarInt().toInt()
		targetId = stream.readDouble().toDouble()
		turnDuration = stream.readUnsignedShort().toInt()
		dispelable = stream.readUnsignedByte().toInt()
		spellId = stream.readVarShort().toInt()
		effectId = stream.readVarInt().toInt()
		parentBoostUid = stream.readVarInt().toInt()
	}
}
