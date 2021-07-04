package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.taxcollector

import fr.lewon.dofus.bot.sniffer.model.TypeManager
import fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.GameRolePlayActorInformations
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class GameRolePlayTaxCollectorInformations : GameRolePlayActorInformations() {

    lateinit var identification: TaxCollectorStaticInformations
    var guildLevel = -1
    var taxCollectorAttack = -1

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        identification = TypeManager.getInstance(stream.readShort())
        identification.deserialize(stream)
        guildLevel = stream.readByte().toUByte().toInt()
        taxCollectorAttack = stream.readInt()
    }
}