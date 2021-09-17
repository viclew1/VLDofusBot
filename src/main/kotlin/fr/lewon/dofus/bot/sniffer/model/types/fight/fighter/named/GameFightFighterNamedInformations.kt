package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named

import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.GameFightFighterInformations
import fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.PlayerStatus
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class GameFightFighterNamedInformations : GameFightFighterInformations() {

    var name = ""
    var status = PlayerStatus()
    var leagueId = 0
    var ladderPosition = 0
    var hiddenInPrefight = false

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        name = stream.readUTF()
        status.deserialize(stream)
        leagueId = stream.readVarShort()
        ladderPosition = stream.readInt()
        hiddenInPrefight = stream.readBoolean()
    }
}