package fr.lewon.dofus.bot.sniffer.model.types.game.alliance

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KohAllianceRoleMembers : NetworkType() {
	var memberCount: Double = 0.0
	var roleAvAId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		memberCount = stream.readVarLong().toDouble()
		roleAvAId = stream.readInt().toInt()
	}
}
