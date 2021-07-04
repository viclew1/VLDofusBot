package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.humanoid

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.human.HumanInformations
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayNamedActorInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

open class GameRolePlayHumanoidInformations : GameRolePlayNamedActorInformations() {

    lateinit var humanoidInfo: HumanInformations
    var accountId = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        humanoidInfo = TypeManager.getInstance(stream.readShort())
        humanoidInfo.deserialize(stream)
        accountId = stream.readInt()
    }
}