package fr.lewon.dofus.bot.sniffer.model.messages.game.finishmoves

import fr.lewon.dofus.bot.sniffer.model.types.game.finishmoves.FinishMoveInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FinishMoveListMessage : NetworkMessage() {
	var finishMoves: ArrayList<FinishMoveInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		finishMoves = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = FinishMoveInformations()
			item.deserialize(stream)
			finishMoves.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4796
}
