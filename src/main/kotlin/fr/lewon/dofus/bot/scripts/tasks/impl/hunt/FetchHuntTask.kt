package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.MapManager
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color

class FetchHuntTask : BooleanDofusBotTask() {

    companion object {
        private const val HUNT_MALL_OUTSIDE_MAP_ID = 142089230.0
        private const val HUNT_MALL_INSIDE_MAP_ID = 128452097.0
        private val HUNT_MALL_CHEST_POINT = PointRelative(0.55443037f, 0.44620255f)
        private val HUNT_SEEK_OPTION_POINT = PointRelative(0.56329113f, 0.4825949f)
        private val HUNT_SEEK_OPTION_MIN_COLOR = Color(0, 0, 0)
        private val HUNT_SEEK_OPTION_MAX_COLOR = Color(25, 25, 25)
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
        MouseUtil.leftClick(gameInfo, HUNT_MALL_CHEST_POINT)
        if (!WaitUtil.waitUntil({ isHuntSeekOptionFound(gameInfo) })) {
            error("Couldn't open chest")
        }
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, HUNT_SEEK_OPTION_POINT)
        TreasureHuntUtil.waitForTreasureHuntUpdate(gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo), 500)
        return ReachMapTask(listOf(outsideMap)).run(logItem, gameInfo)
    }

    private fun isHuntSeekOptionFound(gameInfo: GameInfo): Boolean {
        MouseUtil.move(gameInfo, HUNT_MALL_CHEST_POINT)
        return ScreenUtil.isBetween(
            gameInfo,
            HUNT_SEEK_OPTION_POINT,
            HUNT_SEEK_OPTION_MIN_COLOR,
            HUNT_SEEK_OPTION_MAX_COLOR
        )
    }

    override fun onStarted(): String {
        return "Fetching new treasure hunt ..."
    }
}