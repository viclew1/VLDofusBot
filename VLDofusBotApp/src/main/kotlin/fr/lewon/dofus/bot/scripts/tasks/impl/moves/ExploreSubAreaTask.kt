package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.core.world.Vertex
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightAnyMonsterGroupTask
import fr.lewon.dofus.bot.scripts.tasks.impl.harvest.HarvestAllResourcesTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.util.filemanagers.impl.ExplorationRecordManager
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExploreSubAreaTask(
    private val subArea: DofusSubArea,
    private val killEverything: Boolean,
    private val searchedMonsterName: String,
    private val stopWhenArchMonsterFound: Boolean,
    private val stopWhenWantedMonsterFound: Boolean,
    private val itemIdsToHarvest: List<Double>,
    private val explorationThresholdMinutes: Int
) : DofusBotTask<ExplorationStatus>() {

    companion object {
        val SUB_AREA_ID_FULLY_ALLOWED = listOf(
            99.0, 100.0, 181.0, // Astrub undergrounds
            7.0, // Amakna crypts
            813.0, // Shadow dimension
        )
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo): ExplorationStatus {
        val toExploreMaps = MapManager.getDofusMaps(subArea)
            .filter { it.worldMap != null || SUB_AREA_ID_FULLY_ALLOWED.contains(it.subArea.id) }
            .toMutableList()
        if (toExploreMaps.isEmpty()) {
            error("Nothing to explore in this area")
        }
        val alreadyExploredMaps = toExploreMaps.filter {
            getMinutesSinceLastExploration(it.id) < explorationThresholdMinutes
        }
        ExplorationUIUtil.onAreaExplorationStart(gameInfo.character, subArea)
        var exploredCount = alreadyExploredMaps.size
        val toExploreTotal = toExploreMaps.size
        LastExplorationUiUtil.updateExplorationProgress(
            character = gameInfo.character,
            subArea = subArea,
            current = exploredCount,
            total = toExploreTotal
        )
        toExploreMaps.removeAll(alreadyExploredMaps)
        if (toExploreMaps.isEmpty()) {
            return ExplorationStatus.Finished
        }
        if (!LeaveHavenBagTask().run(logItem, gameInfo)) {
            return ExplorationStatus.NotFinished
        }
        TravelUtil.getPath(gameInfo, toExploreMaps, gameInfo.buildCharacterBasicInfo())
            ?: return ExplorationStatus.Finished
        if (!ReachMapTask(toExploreMaps).run(logItem, gameInfo)) {
            return ExplorationStatus.NotFinished
        }
        if (toExploreMaps.remove(gameInfo.currentMap)) {
            LastExplorationUiUtil.updateExplorationProgress(
                character = gameInfo.character,
                subArea = subArea,
                current = ++exploredCount,
                total = toExploreTotal
            )
        }
        while (toExploreMaps.isNotEmpty()) {
            if (foundSearchedMonster(gameInfo)) {
                return ExplorationStatus.FoundSomething
            }
            if (killEverything) {
                killMonsters(logItem, gameInfo)
            }
            if (!HarvestAllResourcesTask(itemIdsToHarvest).run(logItem, gameInfo)) {
                error("Failed to harvest")
            }
            val characterInfo = gameInfo.buildCharacterBasicInfo(TravelUtil.getAllZaapMaps().map { it.id })
            val nextVertex = getNextVertexToExplore(gameInfo, toExploreMaps, characterInfo)
                ?: return ExplorationStatus.Finished
            val fromVertex = TravelUtil.getCurrentVertex(gameInfo)
            val path = WorldGraphUtil.getPath(listOf(fromVertex), listOf(nextVertex), characterInfo)
                ?: return ExplorationStatus.Finished
            val nextTransition = path.firstOrNull()
                ?: return ExplorationStatus.Finished
            if (!MoveTask(listOf(nextTransition)).run(logItem, gameInfo)) {
                return ExplorationStatus.NotFinished
            }
            if (toExploreMaps.remove(gameInfo.currentMap)) {
                LastExplorationUiUtil.updateExplorationProgress(
                    character = gameInfo.character,
                    subArea = subArea,
                    current = ++exploredCount,
                    total = toExploreTotal
                )
            }
        }
        return ExplorationStatus.Finished
    }

    private fun getNextVertexToExplore(
        gameInfo: GameInfo,
        toExploreMaps: List<DofusMap>,
        characterInfo: DofusCharacterBasicInfo
    ): Vertex? {
        val toExploreMapIds = toExploreMaps.map { it.id }
        val initialVertex = TravelUtil.getCurrentVertex(gameInfo)
        val explored = mutableListOf(initialVertex)
        var frontier = listOf(initialVertex)
        while (frontier.isNotEmpty()) {
            val newFrontier = ArrayList<Vertex>()
            for (vertex in frontier) {
                val newVertices = WorldGraphUtil.getOutgoingEdges(vertex)
                    .filter { edge -> !explored.contains(edge.to) }
                    .flatMap { edge -> WorldGraphUtil.getFilteredTransitions(edge, characterInfo) }
                    .let(WorldGraphUtil::getSortedTransitions)
                    .map { transition -> transition.edge.to }
                    .onEach { destVertex -> explored.add(destVertex) }
                newFrontier.addAll(newVertices)
            }
            val validVertices = newFrontier.filter { it.mapId in toExploreMapIds }
            selectBestVertex(validVertices, toExploreMapIds, characterInfo)?.let {
                return it
            }
            frontier = newFrontier
        }
        return null
    }

    private fun selectBestVertex(
        validVertices: List<Vertex>,
        toExploreMapIds: List<Double>,
        characterInfo: DofusCharacterBasicInfo,
    ): Vertex? {
        return validVertices.minByOrNull { vertex ->
            val newToExploreMapIds = toExploreMapIds.minus(vertex.mapId)
            getSubVertices(vertex, characterInfo).filter { it.mapId in newToExploreMapIds }.size
        }
    }

    private fun getSubVertices(vertex: Vertex, characterInfo: DofusCharacterBasicInfo) =
        WorldGraphUtil.getOutgoingEdges(vertex)
            .flatMap { WorldGraphUtil.getFilteredTransitions(it, characterInfo) }
            .map { it.edge.to }


    private fun getMinutesSinceLastExploration(mapId: Double): Long {
        val lastExplorationTime = ExplorationRecordManager.getLastExplorationTime(mapId) ?: 0L
        val millisSinceLastExploration = System.currentTimeMillis() - lastExplorationTime
        return millisSinceLastExploration / (60_000)
    }

    private fun foundSearchedMonster(gameInfo: GameInfo): Boolean =
        searchedMonsterName.isNotBlank() && gameInfo.monsterInfoByEntityId.values.any { isSearchedMonster(it) }
                || stopWhenArchMonsterFound && gameInfo.monsterInfoByEntityId.values.any { isArchMonster(it) }
                || stopWhenWantedMonsterFound && gameInfo.monsterInfoByEntityId.values.any { isWantedMonster(it) }

    private fun isWantedMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean =
        isAnyMonsterMatchingPredicate(monsterInfo) { it.isQuestMonster }

    private fun isSearchedMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean =
        isAnyMonsterMatchingPredicate(monsterInfo) { it.name.lowercase() == searchedMonsterName.lowercase() }

    private fun isArchMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean =
        isAnyMonsterMatchingPredicate(monsterInfo) { it.isMiniBoss }

    private fun isAnyMonsterMatchingPredicate(
        monsterInfo: GameRolePlayGroupMonsterInformations,
        predicate: (DofusMonster) -> Boolean
    ): Boolean {
        val mainMonster = MonsterManager.getMonster(monsterInfo.staticInfos.mainCreatureLightInfos.genericId.toDouble())
        if (predicate(mainMonster)) {
            return true
        }
        for (underling in monsterInfo.staticInfos.underlings) {
            if (predicate(MonsterManager.getMonster(underling.genericId.toDouble()))) {
                return true
            }
        }
        return false
    }

    private fun killMonsters(logItem: LogItem, gameInfo: GameInfo) {
        while (gameInfo.monsterInfoByEntityId.isNotEmpty()) {
            if (!FightAnyMonsterGroupTask(stopIfNoMonsterPresent = true).run(logItem, gameInfo)) {
                return
            }
        }
    }

    override fun shouldClearSubLogItems(result: ExplorationStatus): Boolean = false

    override fun onStarted(): String {
        return "Exploring sub area [${subArea.label}]"
    }
}