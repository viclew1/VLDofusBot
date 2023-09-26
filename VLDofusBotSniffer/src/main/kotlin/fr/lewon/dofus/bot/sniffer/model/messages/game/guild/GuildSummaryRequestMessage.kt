package fr.lewon.dofus.bot.sniffer.model.messages.game.guild

import fr.lewon.dofus.bot.sniffer.model.messages.game.PaginationRequestAbstractMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class GuildSummaryRequestMessage : PaginationRequestAbstractMessage() {
	var hideFullFilter: Boolean = false
	var followingGuildCriteria: Boolean = false
	var sortDescending: Boolean = false
	var nameFilter: String = ""
	var criterionFilter: ArrayList<Int> = ArrayList()
	var languagesFilter: ArrayList<Int> = ArrayList()
	var recruitmentTypeFilter: ArrayList<Int> = ArrayList()
	var minLevelFilter: Int = 0
	var maxLevelFilter: Int = 0
	var minPlayerLevelFilter: Int = 0
	var maxPlayerLevelFilter: Int = 0
	var minSuccessFilter: Int = 0
	var maxSuccessFilter: Int = 0
	var sortType: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		hideFullFilter = BooleanByteWrapper.getFlag(_box0, 0)
		followingGuildCriteria = BooleanByteWrapper.getFlag(_box0, 1)
		sortDescending = BooleanByteWrapper.getFlag(_box0, 2)
		nameFilter = stream.readUTF()
		criterionFilter = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			criterionFilter.add(item)
		}
		languagesFilter = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			languagesFilter.add(item)
		}
		recruitmentTypeFilter = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readUnsignedByte().toInt()
			recruitmentTypeFilter.add(item)
		}
		minLevelFilter = stream.readUnsignedShort().toInt()
		maxLevelFilter = stream.readUnsignedShort().toInt()
		minPlayerLevelFilter = stream.readUnsignedShort().toInt()
		maxPlayerLevelFilter = stream.readUnsignedShort().toInt()
		minSuccessFilter = stream.readVarInt().toInt()
		maxSuccessFilter = stream.readVarInt().toInt()
		sortType = stream.readUnsignedByte().toInt()
	}
	override fun getNetworkMessageId(): Int = 3550
}
