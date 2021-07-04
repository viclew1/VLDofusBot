package fr.lewon.dofus.bot.sniffer.model.types.actor.roleplay.monster

import fr.lewon.dofus.bot.sniffer.model.types.actor.entity.EntityLook
import fr.lewon.dofus.bot.sniffer.util.ByteArrayReader

class MonsterInGroupInformations : MonsterInGroupLightInformations() {

    lateinit var look: EntityLook

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        look = EntityLook()
        look.deserialize(stream)
    }
}