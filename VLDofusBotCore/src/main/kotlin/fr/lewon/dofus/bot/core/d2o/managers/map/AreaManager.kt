package fr.lewon.dofus.bot.core.d2o.managers.map

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.maps.DofusArea

object AreaManager : VldbManager {

    private lateinit var subAreaById: Map<Double, DofusArea>

    override fun initManager() {
        subAreaById = D2OUtil.getObjects("Areas").associate {
            val id = it["id"].toString().toDouble()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId) ?: "UNKNOWN_AREA_NAME"
            val superAreaId = it["superAreaId"].toString().toInt()
            val hasWorldMap = it["hasWorldMap"].toString().toBoolean()
            id to DofusArea(id, name, superAreaId, hasWorldMap)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getArea(areaId: Double): DofusArea {
        return subAreaById[areaId] ?: error("No area for id : $areaId")
    }

}