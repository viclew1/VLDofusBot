package fr.lewon.dofus.bot.sniffer.model.types.game.guild

import fr.lewon.dofus.bot.sniffer.model.types.game.rank.RankMinimalInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class RankPublicInformation : RankMinimalInformation() {
	var order: Int = 0
	var gfxId: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		order = stream.readVarInt().toInt()
		gfxId = stream.readVarInt().toInt()
	}
}
