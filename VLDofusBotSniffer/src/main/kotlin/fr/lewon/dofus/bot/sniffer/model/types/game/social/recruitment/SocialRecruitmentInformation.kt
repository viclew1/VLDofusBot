package fr.lewon.dofus.bot.sniffer.model.types.game.social.recruitment

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class SocialRecruitmentInformation : NetworkType() {
	var minLevelFacultative: Boolean = false
	var invalidatedByModeration: Boolean = false
	var recruitmentAutoLocked: Boolean = false
	var socialId: Int = 0
	var recruitmentType: Int = 0
	var recruitmentTitle: String = ""
	var recruitmentText: String = ""
	var selectedLanguages: ArrayList<Int> = ArrayList()
	var selectedCriterion: ArrayList<Int> = ArrayList()
	var minLevel: Int = 0
	var lastEditPlayerName: String = ""
	var lastEditDate: Double = 0.0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		val _box0 = stream.readByte()
		minLevelFacultative = BooleanByteWrapper.getFlag(_box0, 0)
		invalidatedByModeration = BooleanByteWrapper.getFlag(_box0, 1)
		recruitmentAutoLocked = BooleanByteWrapper.getFlag(_box0, 2)
		socialId = stream.readVarInt().toInt()
		recruitmentType = stream.readUnsignedByte().toInt()
		recruitmentTitle = stream.readUTF()
		recruitmentText = stream.readUTF()
		selectedLanguages = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			selectedLanguages.add(item)
		}
		selectedCriterion = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarInt().toInt()
			selectedCriterion.add(item)
		}
		minLevel = stream.readUnsignedShort().toInt()
		lastEditPlayerName = stream.readUTF()
		lastEditDate = stream.readDouble().toDouble()
	}
}
