package fr.lewon.dofus.bot.sniffer.model.types.actor

import fr.lewon.dofus.bot.sniffer.model.types.actor.entity.EntityLook
import fr.lewon.dofus.bot.util.io.stream.ByteArrayReader

open class GameContextActorInformations : GameContextActorPositionInformations() {

    lateinit var entityLook: EntityLook

    override fun deserialize(stream: ByteArrayReader) {
        super.deserialize(stream)
        entityLook = EntityLook()
        entityLook.deserialize(stream)
    }
}