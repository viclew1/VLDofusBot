package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.humanoid

import fr.lewon.dofus.bot.sniffer.model.types.actor.ActorAlignmentInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayCharacterInformations : GameRolePlayHumanoidInformations() {

    lateinit var alignmentInfos: ActorAlignmentInformations

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        alignmentInfos = ActorAlignmentInformations()
        alignmentInfos.deserialize(stream)
    }
}