package fr.lewon.dofus.bot.sniffer.model.types.game.context.fight

import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.sniffer.model.messages.NetworkMessage
import fr.lewon.dofus.bot.sniffer.model.types.NetworkType
import fr.lewon.dofus.bot.sniffer.model.ProtocolTypeManager
import fr.lewon.dofus.bot.core.io.stream.BooleanByteWrapper

open class FightExternalInformations : NetworkType() {
	var fightId: Int = 0
	var fightType: Int = 0
	var fightStart: Int = 0
	var fightSpectatorLocked: Boolean = false
	var fightTeams: ArrayList<FightTeamLightInformations> = ArrayList()
	var fightTeamsOptions: ArrayList<FightOptionsInformations> = ArrayList()
	override fun deserialize(stream: ByteArrayReader) {
		super.deserialize(stream)
		fightId = stream.readVarShort().toInt()
		fightType = stream.readUnsignedByte().toInt()
		fightStart = stream.readInt().toInt()
		fightSpectatorLocked = stream.readBoolean()
		fightTeams = ArrayList()
		for (i in 0 until 2) {
			val item = FightTeamLightInformations()
			item.deserialize(stream)
			fightTeams.add(item)
		}
		fightTeamsOptions = ArrayList()
		for (i in 0 until 2) {
			val item = FightOptionsInformations()
			item.deserialize(stream)
			fightTeamsOptions.add(item)
		}
	}
}
