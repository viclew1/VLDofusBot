package fr.lewon.dofus.bot.sniffer.model.types.fight.fighter.ai

import fr.lewon.dofus.bot.sniffer.model.types.actor.ActorAlignmentInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameFightMonsterWithAlignmentInformations : GameFightMonsterInformations() {

    var alignmentInformations = ActorAlignmentInformations()

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        alignmentInformations.deserialize(stream)
    }
}