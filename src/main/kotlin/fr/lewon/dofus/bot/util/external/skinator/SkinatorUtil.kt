package fr.lewon.dofus.bot.util.external.skinator

import fr.lewon.dofus.bot.sniffer.model.types.actor.entity.EntityLook

object SkinatorUtil {

    fun getRealEntityLook(entityLook: EntityLook): EntityLook {
        if (entityLook.skins.isEmpty() && entityLook.subEntities.isNotEmpty()) {
            return entityLook.subEntities.first().entityLook
        }
        return entityLook
    }

}