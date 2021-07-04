package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.npc

import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class GameRolePlayNpcInformations : GameRolePlayActorInformations() {

    var npcId = -1
    var sex = false
    var specialArtworkId = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        npcId = stream.readVarShort()
        sex = stream.readBoolean()
        specialArtworkId = stream.readVarShort()
    }
}