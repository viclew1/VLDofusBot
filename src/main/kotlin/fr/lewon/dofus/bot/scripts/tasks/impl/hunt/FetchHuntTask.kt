package fr.lewon.dofus.bot.scripts.tasks.impl.hunt

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.game.move.transporters.Zaap
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.custom.CustomMoveTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.TravelTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ZaapTowardTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.Color

class FetchHuntTask : DofusBotTask<Boolean>() {

    companion object {
        private val HUNT_MALL_ENTER_POINT_1 = PointRelative(0.53797466f, 0.38449368f)
        private val HUNT_MALL_ENTER_POINT_2 = PointRelative(0.10886077f, 0.42879745f)
        private val HUNT_MALL_EXIT_POINT_1 = PointRelative(0.96455693f, 0.7943038f)
        private val HUNT_MALL_EXIT_POINT_2 = PointRelative(0.9721519f, 0.6914557f)
        private val HUNT_MALL_CHEST_POINT = PointRelative(0.55443037f, 0.44620255f)
        private val HUNT_SEEK_OPTION_POINT = PointRelative(0.56329113f, 0.4825949f)
        private val HUNT_SEEK_OPTION_MIN_COLOR = Color(0, 0, 0)
        private val HUNT_SEEK_OPTION_MAX_COLOR = Color(25, 25, 25)
    }

    override fun execute(logItem: LogItem): Boolean {
        GameInfo.treasureHunt = null
        if (TreasureHuntUtil.isHuntPresent()) {
            return true
        }
        ZaapTowardTask(Zaap.CANIA_CHAMPS_CANIA).run(logItem)
        TravelTask(DofusCoordinates(-24, -36)).run(logItem)
        CustomMoveTask(HUNT_MALL_ENTER_POINT_1).run(logItem)
        CustomMoveTask(HUNT_MALL_ENTER_POINT_2).run(logItem)
        WaitUtil.sleep(2000)
        MouseUtil.leftClick(HUNT_MALL_CHEST_POINT)
        ScreenUtil.waitForColor(HUNT_SEEK_OPTION_POINT, HUNT_SEEK_OPTION_MIN_COLOR, HUNT_SEEK_OPTION_MAX_COLOR)
        MouseUtil.leftClick(HUNT_SEEK_OPTION_POINT, false, 0)
        WaitUtil.waitUntil({ TreasureHuntUtil.isHuntPresent() })
        TreasureHuntUtil.updatePoints()
        CustomMoveTask(HUNT_MALL_EXIT_POINT_1).run(logItem)
        CustomMoveTask(HUNT_MALL_EXIT_POINT_2).run(logItem)
        return true
    }

    override fun onStarted(): String {
        return "Fetching new treasure hunt ..."
    }
}