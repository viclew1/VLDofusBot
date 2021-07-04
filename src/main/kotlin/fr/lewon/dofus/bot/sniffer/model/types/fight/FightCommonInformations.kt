package fr.lewon.dofus.bot.sniffer.model.types.fight

import fr.lewon.dofus.bot.sniffer.model.INetworkType
import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class FightCommonInformations : INetworkType {

    var fightId = -1
    var fightType = -1
    var fightTeams = ArrayList<FightTeamInformations>()
    var fightTeamsPositions = ArrayList<Int>()
    var fightTeamOptions = ArrayList<FightOptionsInformations>()

    override fun deserialize(stream: ByteArrayReader) {
        fightId = stream.readVarShort()
        fightType = stream.readByte().toInt()
        for (i in 0 until stream.readShort()) {
            val fightTeam = TypeManager.getInstance<FightTeamInformations>(stream.readShort())
            fightTeam.deserialize(stream)
            fightTeams.add(fightTeam)
        }
        for (i in 0 until stream.readShort()) {
            fightTeamsPositions.add(stream.readVarShort())
        }
        for (i in 0 until stream.readShort()) {
            val fightOptions = TypeManager.getInstance<FightOptionsInformations>(stream.readShort())
            fightOptions.deserialize(stream)
            fightTeamOptions.add(fightOptions)
        }
    }
}