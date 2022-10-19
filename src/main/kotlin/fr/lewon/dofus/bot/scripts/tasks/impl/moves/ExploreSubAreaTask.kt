package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.entity.MonsterManager
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightMonsterGroupTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.GameRolePlayGroupMonsterInformations
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExploreSubAreaTask(
    private val subArea: DofusSubArea,
    private val killEverything: Boolean,
    private val searchedMonsterName: String,
    private val stopWhenArchMonsterFound: Boolean,
    private val stopWhenWantedMonsterFound: Boolean,
    private val runForever: Boolean
) : BooleanDofusBotTask() {

    companion object {
        val SUB_AREA_ID_FULLY_ALLOWED = listOf(
            99.0, 100.0, 181.0, // Astrub undergrounds
            7.0, // Amakna crypts
            813.0, // Shadow dimension
        )
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val initialExploreMapsList = MapManager.getDofusMaps(subArea)
            .filter { it.worldMap != null || SUB_AREA_ID_FULLY_ALLOWED.contains(it.subArea.id) }
        if (initialExploreMapsList.isEmpty()) {
            error("Nothing to explore in this area")
        }
        var toExploreMaps = initialExploreMapsList.toMutableList()
        if (!ReachMapTask(toExploreMaps).run(logItem, gameInfo)) {
            error("Couldn't reach area")
        }
        toExploreMaps.remove(gameInfo.currentMap)
        var success = false
        while (toExploreMaps.isNotEmpty()) {
            if (foundSearchedMonster(gameInfo)) {
                return true
            }
            if (killEverything) {
                killMonsters(logItem, gameInfo)
            }
            if (!TravelTask(toExploreMaps).run(logItem, gameInfo)) {
                if (success && runForever) {
                    toExploreMaps = initialExploreMapsList.toMutableList()
                } else {
                    error("Failed to move")
                }
            }
            success = true
            toExploreMaps.remove(gameInfo.currentMap)
            if (toExploreMaps.isEmpty() && runForever) {
                toExploreMaps = initialExploreMapsList.toMutableList()
            }
        }
        return success
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

    override fun onStarted(): String {
        return "Exploring sub area [${subArea.area.name} (${subArea.name})]"
    }

}