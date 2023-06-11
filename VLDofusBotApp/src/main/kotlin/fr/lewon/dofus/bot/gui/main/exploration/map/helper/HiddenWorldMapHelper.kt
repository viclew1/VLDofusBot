package fr.lewon.dofus.bot.gui.main.exploration.map.helper

import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.util.UiResource

object HiddenWorldMapHelper : WorldMapHelper("Hidden world map", UiResource.LOWER_LAYER) {

    override fun isMapValid(map: DofusMap): Boolean =
        isDisplayedOnMap(map.subArea) && map.canFightMonster && map.canSpawnMonsters

    override fun isDisplayedOnMap(subArea: DofusSubArea): Boolean = validSubAreaIds.contains(subArea.id)

    override fun getPriorityMap(maps: List<DofusMap>): DofusMap? = maps.minByOrNull { it.subArea.mapIds.size }

}

private val validSubAreaIds = listOf(
    816.0, 461.0, 469.0, 314.0, 316.0, 495.0, 492.0, 7.0, 181.0, 100.0, 99.0, 1011.0, 985.0, 468.0
)