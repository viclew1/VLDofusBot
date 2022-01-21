package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.d2o.managers.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.network.GameInfo

class ExploreSubAreaTask(
    private val subArea: DofusSubArea,
    private val worldMap: Int,
) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val toExploreMaps = MapManager.getDofusMaps(subArea)
            .filter { it.worldMap == worldMap }
            .toMutableList()
        if (!ReachMapTask(toExploreMaps).run(logItem, gameInfo)) {
            error("Couldn't reach area")
        }
        toExploreMaps.remove(gameInfo.currentMap)
        while (toExploreMaps.isNotEmpty()) {
            if (!TravelTask(toExploreMaps).run(logItem, gameInfo)) {
                error("Failed to move")
            }
            toExploreMaps.remove(gameInfo.currentMap)
        }
        return true
    }

    override fun onStarted(): String {
        return "Exploring sub area [${subArea.area.name} (${subArea.name})]"
    }

}