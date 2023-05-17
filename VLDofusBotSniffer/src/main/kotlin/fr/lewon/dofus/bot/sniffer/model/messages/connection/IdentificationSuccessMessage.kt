package fr.lewon.dofus.bot.sniffer.model.messages.connection

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class IdentificationSuccessMessage : NetworkMessage() {
	var hasRights: Boolean = false
	var hasForceRight: Boolean = false
	var wasAlreadyConnected: Boolean = false
	var login: String = ""
	lateinit var accountTag: AccountTagInformation
	var accountId: Int = 0
	var communityId: Int = 0
	var accountCreation: Double = 0.0
	var subscriptionEndDate: Double = 0.0
	var havenbagAvailableRoom: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		hasRights = BooleanByteWrapper.getFlag(_box0, 0)
		hasForceRight = BooleanByteWrapper.getFlag(_box0, 1)
		wasAlreadyConnected = BooleanByteWrapper.getFlag(_box0, 2)
		login = stream.readUTF()
		accountTag = AccountTagInformation()
		accountTag.deserialize(stream)
		accountId = stream.readInt().toInt()
		communityId = stream.readUnsignedByte().toInt()
		accountCreation = stream.readDouble().toDouble()
		subscriptionEndDate = stream.readDouble().toDouble()
		havenbagAvailableRoom = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 7045
}
