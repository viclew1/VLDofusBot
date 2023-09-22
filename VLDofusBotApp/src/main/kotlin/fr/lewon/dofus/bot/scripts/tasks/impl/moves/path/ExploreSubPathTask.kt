package fr.lewon.dofus.bot.scripts.tasks.impl.moves.path

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.model.characters.paths.SubPath
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.SingleExplorationTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExploreSubPathTask(
    subPath: SubPath,
    explorationParameters: ExplorationParameters
) : SingleExplorationTask<SubPath>(subPath, explorationParameters) {

    private val nextMapsToExplore = subPath.mapIds.map(MapManager::getDofusMap).toMutableList()

    override fun onExplorationStart(gameInfo: GameInfo, itemToExplore: SubPath, alreadyExploredMaps: List<DofusMap>) {
        while (nextMapsToExplore.firstOrNull() in alreadyExploredMaps) {
            nextMapsToExplore.removeFirstOrNull()
        }
    }

    override fun getMapsToExplore(itemToExplore: SubPath): List<DofusMap> = nextMapsToExplore

    override fun buildOnStartedMessage(itemToExplore: SubPath): String =
        "Exploring sub path [${itemToExplore.displayName}]"

    override fun getNextMapsToExplore(
        toExploreMaps: List<DofusMap>,
        alreadyExploredMaps: List<DofusMap>
    ): List<DofusMap> = nextMapsToExplore.removeFirstOrNull()?.let(::listOf) ?: emptyList()
}