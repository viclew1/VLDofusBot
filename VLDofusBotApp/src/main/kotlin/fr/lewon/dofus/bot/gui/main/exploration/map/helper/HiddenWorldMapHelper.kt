package fr.lewon.dofus.bot.gui.main.exploration.map.helper

import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea

object HiddenWorldMapHelper : WorldMapHelper() {

    override fun isMapValid(map: DofusMap): Boolean =
        isDisplayedOnMap(map.subArea) && map.canFightMonster && map.canSpawnMonsters

    override fun isDisplayedOnMap(subArea: DofusSubArea): Boolean = validSubAreaIds.contains(subArea.id)

    override fun getPriorityMap(maps: List<DofusMap>): DofusMap? = maps.minByOrNull { it.subArea.mapIds.size }

}

val validSubAreaIds = listOf(
    816.0, 461.0, 469.0, 314.0, 316.0, 495.0, 492.0, 7.0, 181.0, 100.0, 99.0, 1011.0, 985.0, 468.0
)

fun main() {
    VldbCoreInitializer.initAll()
    D2OUtil.getObjects("MapPositions").forEach {
        if (it["id"].toString().toDouble() == 87558148.0) {
            val x = it["posX"]
            val y = it["posY"]
            println("$x / $y - $it")
        }
    }
}