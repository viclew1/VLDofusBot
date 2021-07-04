package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.npc

import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayNpcWithQuestInformations : GameRolePlayNpcInformations() {

    lateinit var questFlag: GameRolePlayNpcQuestFlag

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        questFlag = GameRolePlayNpcQuestFlag()
        questFlag.deserialize(stream)
    }
}