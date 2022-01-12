package fr.lewon.dofus.bot.model.maps

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.manager.d2o.managers.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap

data class MapInformation(
    var mapId: Double = -1.0,
    var subAreaId: Int = -1
) {
    @JsonIgnore
    fun getMap(): DofusMap {
        return MapManager.getDofusMap(mapId)
    }
}