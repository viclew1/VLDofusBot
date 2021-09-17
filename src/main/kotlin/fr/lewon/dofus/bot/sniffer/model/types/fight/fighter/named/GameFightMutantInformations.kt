package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.named

import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightMutantInformations : GameFightFighterNamedInformations() {

    var powerLevel = 0

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        powerLevel = stream.readByte().toInt()
    }
}