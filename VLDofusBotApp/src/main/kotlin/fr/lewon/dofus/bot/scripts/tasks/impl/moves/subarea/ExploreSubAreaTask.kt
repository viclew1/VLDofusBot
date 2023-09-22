package fr.lewon.dofus.bot.scripts.tasks.impl.moves.subarea

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.SingleExplorationTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExploreSubAreaTask(
    subArea: DofusSubArea,
    explorationParameters: ExplorationParameters
) : SingleExplorationTask<DofusSubArea>(subArea, explorationParameters) {

    companion object {

        val SUB_AREA_ID_FULLY_ALLOWED = listOf(
            99.0, 100.0, 181.0, // Astrub undergrounds
            7.0, // Amakna crypts
            813.0, // Shadow dimension
        )
    }

    override fun onExplorationStart(
        gameInfo: GameInfo,
        itemToExplore: DofusSubArea,
        alreadyExploredMaps: List<DofusMap>
    ) = ExplorationUIUtil.onExplorationStart(gameInfo.character, itemToExplore)

    override fun getMapsToExplore(itemToExplore: DofusSubArea): List<DofusMap> = MapManager.getDofusMaps(itemToExplore)
        .filter { it.worldMap != null || SUB_AREA_ID_FULLY_ALLOWED.contains(it.subArea.id) }

    override fun buildOnStartedMessage(itemToExplore: DofusSubArea): String =
        "Exploring sub area [${itemToExplore.label}]"

    override fun getNextMapsToExplore(
        toExploreMaps: List<DofusMap>,
        alreadyExploredMaps: List<DofusMap>
    ): List<DofusMap> {
        return toExploreMaps.distinct().minus(alreadyExploredMaps.toSet())
    }
}