package fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.exchanges

import fr.lewon.dofus.bot.sniffer.model.types.game.mount.UpdateMountCharacteristic
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class UpdateMountCharacteristicsMessage : NetworkMessage() {
	var rideId: Int = 0
	var boostToUpdateList: ArrayList<UpdateMountCharacteristic> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		rideId = stream.readVarInt().toInt()
		boostToUpdateList = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<UpdateMountCharacteristic>(stream.readUnsignedShort())
			item.deserialize(stream)
			boostToUpdateList.add(item)
		}
	}
	override fun getNetworkMessageId(): Int = 8598
}
