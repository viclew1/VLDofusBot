package fr.lewon.dofus.bot.sniffer.model.messages.game.alliance.summary

import fr.lewon.dofus.bot.sniffer.model.messages.game.PaginationRequestAbstractMessage
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class AllianceSummaryRequestMessage : PaginationRequestAbstractMessage() {
	var hideFullFilter: Boolean = false
	var followingAllianceCriteria: Boolean = false
	var sortDescending: Boolean = false
	var filterType: Int = 0
	var textFilter: String = ""
	var criterionFilter: ArrayList<Int> = ArrayList()
	var sortType: Int = 0
	var languagesFilter: ArrayList<Int> = ArrayList()
	var recruitmentTypeFilter: ArrayList<Int> = ArrayList()
	var minPlayerLevelFilter: Int = 0
	var maxPlayerLevelFilter: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		hideFullFilter = BooleanByteWrapper.getFlag(_box0, 0)
		followingAllianceCriteria = BooleanByteWrapper.getFlag(_box0, 1)
		sortDescending = BooleanByteWrapper.getFlag(_box0, 2)
		filterType = stream.readInt().toInt()
		textFilter = stream.readUTF()
		criterionFilter = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			criterionFilter.add(item)
		}
		sortType = stream.readUnsignedByte().toInt()
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
		minPlayerLevelFilter = stream.readUnsignedShort().toInt()
		maxPlayerLevelFilter = stream.readUnsignedShort().toInt()
	}
	override fun getNetworkMessageId(): Int = 8794
}
