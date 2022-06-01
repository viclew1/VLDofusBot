package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.hunt.HuntLevel
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class FetchHuntTask(private val huntLevel: HuntLevel) : BooleanDofusBotTask() {

    companion object {
        private const val HUNT_MALL_OUTSIDE_MAP_ID = 142089230.0
        private const val HUNT_MALL_INSIDE_MAP_ID = 128452097.0
        private const val HUNT_CHEST_ELEMENT_ID = 484993
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val outsideMap = MapManager.getDofusMap(HUNT_MALL_OUTSIDE_MAP_ID)
        val insideMap = MapManager.getDofusMap(HUNT_MALL_INSIDE_MAP_ID)
        if (!ReachMapTask(listOf(outsideMap)).run(logItem, gameInfo)) {
            return false
        }
        if (!ReachMapTask(listOf(insideMap)).run(logItem, gameInfo)) {
            return false
        }
        InteractiveUtil.useInteractive(gameInfo, HUNT_CHEST_ELEMENT_ID, huntLevel.skillId)
        TreasureHuntUtil.waitForTreasureHuntUpdate(gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 500)
        return ReachMapTask(listOf(outsideMap)).run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        return "Fetching new treasure hunt ..."
    }
}