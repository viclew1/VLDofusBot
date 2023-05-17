package fr.lewon.dofus.bot.core.d2o.managers.map

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.maps.DofusWorldMap

object WorldMapManager : VldbManager {

    private lateinit var worldMapById: Map<Int, DofusWorldMap>

    override fun initManager() {
        val objects = D2OUtil.getObjects("WorldMaps")
        worldMapById = objects.associate {
            val id = it["id"].toString().toInt()
            val nameId = it["nameId"].toString().toInt()
            val name = I18NUtil.getLabel(nameId) ?: "INVALID_WORLD_MAP_NAME"
            val visibleOnMap = it["visibleOnMap"].toString().toBoolean()
            val viewableEverywhere = it["viewableEverywhere"].toString().toBoolean()
            id to DofusWorldMap(id, name, visibleOnMap, viewableEverywhere)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getWorldMap(id: Int): DofusWorldMap? {
        return worldMapById[id]
    }

    fun getAllWorldMaps(): List<DofusWorldMap> {
        return worldMapById.values.toList()
    }

}