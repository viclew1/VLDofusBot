package fr.lewon.dofus.bot.model.maps

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.core.manager.DofusMapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap

data class MapInformation(
    var mapId: Double = -1.0,
    var subAreaId: Int = -1
) {
    @JsonIgnore
    fun getMap(): DofusMap {
        return DofusMapManager.getDofusMap(mapId)
    }
}