package fr.lewon.dofus.bot.core.d2o.managers.entity

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.entity.DofusNPC

object NpcManager : VldbManager {

    private lateinit var npcById: Map<Double, DofusNPC>

    override fun initManager() {
        npcById = D2OUtil.getObjects("Npcs").associate {
            val id = it["id"].toString().toDouble()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId) ?: "UNKNOWN_NPC_NAME"
            id to DofusNPC(id, name)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getNPC(id: Double): DofusNPC {
        return npcById[id] ?: error("No NPC for id : $id")
    }

}
