package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.world.Vertex
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightAnyMonsterGroupTask
import fr.lewon.dofus.bot.scripts.tasks.impl.harvest.TransferItemsToBankTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.util.ExplorationParameters
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.LeaveHavenBagTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.util.filemanagers.impl.ExplorationRecordManager
import fr.lewon.dofus.bot.util.game.TravelUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

abstract class SingleExplorationTask<T>(
    private val itemToExplore: T,
    private val explorationParameters: ExplorationParameters,
) : DofusBotTask<ExplorationStatus>() {

    protected abstract fun getMapsToExplore(itemToExplore: T): List<DofusMap>

    protected abstract fun onExplorationStart(gameInfo: GameInfo, itemToExplore: T, alreadyExploredMaps: List<DofusMap>)

    protected abstract fun buildOnStartedMessage(itemToExplore: T): String

    protected abstract fun getNextMapsToExplore(
        toExploreMaps: List<DofusMap>,
        alreadyExploredMaps: List<DofusMap>
    ): List<DofusMap>

    override fun execute(logItem: LogItem, gameInfo: GameInfo): ExplorationStatus {
        val toExploreMaps = getMapsToExplore(itemToExplore).toMutableList()
        if (toExploreMaps.isEmpty()) {
            error("No map to explore found")
        }
        val alreadyExploredMaps = toExploreMaps.filter {
            getMinutesSinceLastExploration(it.id) < explorationParameters.explorationThresholdMinutes
        }
        onExplorationStart(gameInfo, itemToExplore, alreadyExploredMaps)
        var exploredCount = alreadyExploredMaps.size
        val toExploreTotal = toExploreMaps.size
        LastExplorationUiUtil.updateExplorationProgress(
            character = gameInfo.character,
            item = itemToExplore,
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
        val initialMapsToExplore = getNextMapsToExplore(toExploreMaps, alreadyExploredMaps)
        TravelUtil.getPath(gameInfo, initialMapsToExplore, gameInfo.buildCharacterBasicInfo())
            ?: return ExplorationStatus.Finished
        val availableZaaps = if (explorationParameters.useZaaps) TravelUtil.getAllZaapMaps() else emptyList()
        if (!ReachMapTask(initialMapsToExplore, availableZaaps).run(logItem, gameInfo)) {
            return ExplorationStatus.NotFinished
        }
        if (toExploreMaps.remove(gameInfo.currentMap)) {
            LastExplorationUiUtil.updateExplorationProgress(
                character = gameInfo.character,
                item = itemToExplore,
                current = ++exploredCount,
                total = toExploreTotal
            )
        }
        if (foundSearchedMonster(gameInfo)) {
            return ExplorationStatus.FoundSomething
        }
        while (toExploreMaps.isNotEmpty()) {
            exploreMap(logItem, gameInfo)
            val characterInfo = gameInfo.buildCharacterBasicInfo(TravelUtil.getAllZaapMaps().map { it.id })
            val nextMapsToExplore = getNextMapsToExplore(toExploreMaps, alreadyExploredMaps)
            val nextVertex = getNextVertexToExplore(gameInfo, nextMapsToExplore, characterInfo)
                ?: return ExplorationStatus.Finished
            val fromVertex = TravelUtil.getCurrentVertex(gameInfo)
            val path = WorldGraphUtil.getPath(listOf(fromVertex), listOf(nextVertex), characterInfo)
                ?: return ExplorationStatus.Finished
            val nextTransition = path.firstOrNull()
                ?: return ExplorationStatus.Finished
            if (!MoveTask(listOf(nextTransition)).run(logItem, gameInfo)) {
                return ExplorationStatus.NotFinished
            }
            if (foundSearchedMonster(gameInfo)) {
                return ExplorationStatus.FoundSomething
            }
            if (toExploreMaps.remove(gameInfo.currentMap)) {
                LastExplorationUiUtil.updateExplorationProgress(
                    character = gameInfo.character,
                    item = itemToExplore,
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
        characterInfo: DofusCharacterBasicInfo,
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

    //TODO this must move to the MoveTask once the fight parameters become global parameters
    private fun exploreMap(logItem: LogItem, gameInfo: GameInfo) {
        returnToBankIfNeeded(logItem, gameInfo)
        if (explorationParameters.killEverything) {
            killMonsters(logItem, gameInfo)
        }
        returnToBankIfNeeded(logItem, gameInfo)
    }

    private fun killMonsters(logItem: LogItem, gameInfo: GameInfo) {
        while (gameInfo.monsterInfoByEntityId.isNotEmpty()) {
            if (!FightAnyMonsterGroupTask(
                    stopIfNoMonsterPresent = true,
                    maxMonsterGroupLevel = explorationParameters.maxMonsterGroupLevel,
                    maxMonsterGroupSize = explorationParameters.maxMonsterGroupSize
                ).run(logItem, gameInfo)
            ) {
                return
            }
        }
    }

    private fun returnToBankIfNeeded(logItem: LogItem, gameInfo: GameInfo) {
        if (gameInfo.shouldReturnToBank()) {
            if (!TransferItemsToBankTask().run(logItem, gameInfo)) {
                error("Couldn't transfer items to bank")
            }
        }
    }

    private fun foundSearchedMonster(gameInfo: GameInfo): Boolean {
        val searchedMonsterName = explorationParameters.searchedMonsterName
        val stopWhenArchMonsterFound = explorationParameters.stopWhenArchMonsterFound
        val stopWhenWantedMonsterFound = explorationParameters.stopWhenWantedMonsterFound
        return searchedMonsterName.isNotBlank() && gameInfo.monsterInfoByEntityId.values.any { isSearchedMonster(it) }
            || stopWhenArchMonsterFound && gameInfo.monsterInfoByEntityId.values.any { isArchMonster(it) }
            || stopWhenWantedMonsterFound && gameInfo.monsterInfoByEntityId.values.any { isWantedMonster(it) }
    }

    private fun isWantedMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean =
        isAnyMonsterMatchingPredicate(monsterInfo) { it.isQuestMonster }

    private fun isSearchedMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean =
        isAnyMonsterMatchingPredicate(monsterInfo) {
            it.name.lowercase() == explorationParameters.searchedMonsterName.lowercase()
        }

    private fun isArchMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean =
        isAnyMonsterMatchingPredicate(monsterInfo) { it.isMiniBoss }

    private fun isAnyMonsterMatchingPredicate(
        monsterInfo: GameRolePlayGroupMonsterInformations,
        predicate: (DofusMonster) -> Boolean,
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

    override fun shouldClearSubLogItems(result: ExplorationStatus): Boolean = false

    override fun onStarted(): String = buildOnStartedMessage(itemToExplore)
}

