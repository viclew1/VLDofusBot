package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightMonsterGroupTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ExploreSubAreaTask(
    private val subArea: DofusSubArea,
    private val worldMap: Int,
    private val killEverything: Boolean = false
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val toExploreMaps = MapManager.getDofusMaps(subArea)
            .filter { it.worldMap == worldMap }
            .toMutableList()
        if (!ReachMapTask(toExploreMaps).run(logItem, gameInfo)) {
            error("Couldn't reach area")
        }
        toExploreMaps.remove(gameInfo.currentMap)
        var success = false
        while (toExploreMaps.isNotEmpty()) {
            if (killEverything) {
                killMonsters(logItem, gameInfo)
            }
            if (!TravelTask(toExploreMaps).run(logItem, gameInfo)) {
                error("Failed to move")
            }
            success = true
            toExploreMaps.remove(gameInfo.currentMap)
        }
        return success
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