package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GameRolePlayShowMultipleActorsMessage : NetworkMessage() {
	var informationsList: ArrayList<GameRolePlayActorInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		informationsList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<GameRolePlayActorInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			informationsList.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8352
}
