package fr.lewon.dofus.bot.sniffer.model.types.game.alliance

import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.AllianceInformation
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class KohAllianceInfo : NetworkType() {
	lateinit var alliance: AllianceInformation
	var memberCount: Double = 0.0
	var kohAllianceRoleMembers: ArrayList<KohAllianceRoleMembers> = ArrayList()
	var scores: ArrayList<KohScore> = ArrayList()
	var matchDominationScores: Int = 0
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		alliance = AllianceInformation()
		alliance.deserialize(stream)
		memberCount = stream.readVarLong().toDouble()
		kohAllianceRoleMembers = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = KohAllianceRoleMembers()
			item.deserialize(stream)
			kohAllianceRoleMembers.add(item)
		}
		scores = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = KohScore()
			item.deserialize(stream)
			scores.add(item)
		}
		matchDominationScores = stream.readVarInt().toInt()
	}
}
