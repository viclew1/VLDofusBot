package fr.lewon.dofus.bot.util.external.skinator

import fr.lewon.dofus.bot.sniffer.model.types.game.look.EntityLook

object SkinatorUtil {

    fun getRealEntityLook(entityLook: EntityLook): EntityLook {
        if (entityLook.skins.isEmpty() && entityLook.subentities.isNotEmpty()) {
            return entityLook.subentities.first().subEntityLook
        }
        return entityLook
    }

}