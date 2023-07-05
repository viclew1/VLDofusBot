package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.ExplorationUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.lastexploration.LastExplorationUiUtil
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightMonsterGroupTask
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
        LeaveHavenBagTask().run(logItem, gameInfo)
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
            TravelUtil.getPath(gameInfo, toExploreMaps, gameInfo.buildCharacterBasicInfo())
                ?: return ExplorationStatus.Finished
            if (!ReachMapTask(toExploreMaps, emptyList()).run(logItem, gameInfo)) {
                return ExplorationStatus.NotFinished
            }
            toExploreMaps.remove(gameInfo.currentMap)
            LastExplorationUiUtil.updateExplorationProgress(
                character = gameInfo.character,
                subArea = subArea,
                current = ++exploredCount,
                total = toExploreTotal
            )
        }
        return ExplorationStatus.Finished
    }

    private fun getMinutesSinceLastExploration(mapId: Double): Long {
        val lastExplorationTime = ExplorationRecordManager.getLastExplorationTime(mapId) ?: 0L
        val millisSinceLastExploration = System.currentTimeMillis() - lastExplorationTime
        return millisSinceLastExploration / (60_000)
    }

    private fun foundSearchedMonster(gameInfo: GameInfo): Boolean {
        return searchedMonsterName.isNotBlank() && gameInfo.monsterInfoByEntityId.values.any { isSearchedMonster(it) }
                || stopWhenArchMonsterFound && gameInfo.monsterInfoByEntityId.values.any { isArchMonster(it) }
                || stopWhenWantedMonsterFound && gameInfo.monsterInfoByEntityId.values.any { isWantedMonster(it) }
    }

    private fun isWantedMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean {
        return isAnyMonsterMatchingPredicate(monsterInfo) { it.isQuestMonster }
    }

    private fun isSearchedMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean {
        return isAnyMonsterMatchingPredicate(monsterInfo) { it.name.lowercase() == searchedMonsterName.lowercase() }
    }

    private fun isArchMonster(monsterInfo: GameRolePlayGroupMonsterInformations): Boolean {
        return isAnyMonsterMatchingPredicate(monsterInfo) { it.isMiniBoss }
    }

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
            if (!FightMonsterGroupTask(stopIfNoMonsterPresent = true).run(logItem, gameInfo)) {
                return
            }
        }
    }

    override fun shouldClearSubLogItems(result: ExplorationStatus): Boolean = false

    override fun onStarted(): String {
        return "Exploring sub area [${subArea.label}]"
    }
}