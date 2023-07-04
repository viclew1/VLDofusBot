package fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.havenbag

import fr.lewon.dofus.bot.sniffer.model.types.game.guild.HavenBagFurnitureInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class HavenBagFurnituresMessage : NetworkMessage() {
	var furnituresInfos: ArrayList<HavenBagFurnitureInformation> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		furnituresInfos = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = HavenBagFurnitureInformation()
			item.deserialize(stream)
			furnituresInfos.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 4362
}
