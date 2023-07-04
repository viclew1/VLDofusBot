package fr.lewon.dofus.bot.sniffer.model.messages.game.basic

import fr.lewon.dofus.bot.sniffer.model.types.common.AccountTagInformation
import fr.lewon.dofus.bot.sniffer.model.types.game.social.AbstractSocialGroupInfos
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class BasicWhoIsMessage : NetworkMessage() {
	var self: Boolean = false
	var verbose: Boolean = false
	var position: Int = 0
	lateinit var accountTag: AccountTagInformation
	var accountId: Int = 0
	var playerName: String = ""
	var playerId: Double = 0.0
	var areaId: Int = 0
	var serverId: Int = 0
	var originServerId: Int = 0
	var socialGroups: ArrayList<AbstractSocialGroupInfos> = ArrayList()
	var playerState: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		self = BooleanByteWrapper.getFlag(_box0, 0)
		verbose = BooleanByteWrapper.getFlag(_box0, 1)
		position = stream.readUnsignedByte().toInt()
		accountTag = AccountTagInformation()
		accountTag.deserialize(stream)
		accountId = stream.readInt().toInt()
		playerName = stream.readUTF()
		playerId = stream.readVarLong().toDouble()
		areaId = stream.readUnsignedShort().toInt()
		serverId = stream.readUnsignedShort().toInt()
		originServerId = stream.readUnsignedShort().toInt()
		socialGroups = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<AbstractSocialGroupInfos>(stream.readUnsignedShort())
			item.deserialize(stream)
			socialGroups.add(item)
		}
		playerState = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 7264
}
