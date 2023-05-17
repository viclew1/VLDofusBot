package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightCommonInformations : NetworkType() {
	var fightId: Int = 0
	var fightType: Int = 0
	var fightTeams: ArrayList<FightTeamInformations> = ArrayList()
	var fightTeamsPositions: ArrayList<Int> = ArrayList()
	var fightTeamsOptions: ArrayList<FightOptionsInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		fightType = stream.readUnsignedByte().toInt()
		fightTeams = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = ProtocolTypeManager.getInstance<FightTeamInformations>(stream.readUnsignedShort())
			item.deserialize(stream)
			fightTeams.add(item)
		}
		fightTeamsPositions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = stream.readVarShort().toInt()
			fightTeamsPositions.add(item)
		}
		fightTeamsOptions = ArrayList()
		for (i in 0 until stream.readUnsignedShort().toInt()) {
			val item = FightOptionsInformations()
			item.deserialize(stream)
			fightTeamsOptions.add(item)
		}
	}
}
