package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.prism

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayPrismInformations : GameRolePlayActorInformations() {

    lateinit var prismInformation: PrismInformation

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        prismInformation = TypeManager.getInstance(stream.readShort())
        prismInformation.deserialize(stream)
    }
}