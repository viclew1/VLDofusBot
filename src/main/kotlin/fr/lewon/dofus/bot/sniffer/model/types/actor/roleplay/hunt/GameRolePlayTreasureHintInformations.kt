package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.hunt

import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameRolePlayTreasureHintInformations : GameRolePlayActorInformations() {

    var npcId = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        npcId = stream.readVarShort()
    }
}