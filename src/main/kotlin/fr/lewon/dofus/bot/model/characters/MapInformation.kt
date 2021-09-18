package fr.lewon.dofus.bot.model.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.util.d2o.DofusMapManager

class MapInformation(
    var mapId: Double = -1.0,
    var subAreaId: Int = -1
) {
    @JsonIgnore
    fun getMap(): DofusMap {
        return DofusMapManager.getDofusMap(mapId)
    }
}