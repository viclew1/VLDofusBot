package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.portal

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

class GameRolePlayPortalInformations : GameRolePlayActorInformations() {

    lateinit var portal: PortalInformation

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        portal = TypeManager.getInstance(stream.readUnsignedShort())
        portal.deserialize(stream)
    }

}