package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.RefreshHuntTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step.ExecuteFightHuntStepTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step.ExecuteNpcHuntStepTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step.ExecutePoiHuntStepTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStep
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFight
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.Color

object TreasureHuntUtil {

    private val REF_TOP_LEFT_HUNT_POINT = PointRelative(-0.27520758f, 0.14222223f)
    private val REF_GIVE_UP_HUNT_POINT = PointRelative(-0.03321471f, 0.19111112f)
    private val REF_FIGHT_POINT = PointRelative(-0.2019774f, 0.2627866f)
    private val REF_DISPLAY_BORDER_POINT = PointRelative(-0.080681816f, 0.3849432f)
    private val REF_FIRST_FLAG_POINT = PointRelative(-0.03084223f, 0.26222223f)
    private val REF_SIXTH_FLAG_POINT = PointRelative(-0.03084223f, 0.40564373f)
    private val REF_SEARCH_POINT = PointRelative(-0.042372882f, 0.43915343f)

    private val REF_DISPLAY_BORDERS_FROM_FLAG_X = REF_DISPLAY_BORDER_POINT.x - REF_FIRST_FLAG_POINT.x
    private const val FLAG_WIDTH = 0.02f
    private const val FLAG_HALF_WIDTH = FLAG_WIDTH / 2f
    private val FLAG_DELTA_Y = (REF_SIXTH_FLAG_POINT.y - REF_FIRST_FLAG_POINT.y) / 5

    private val REF_DELTA_GIVE_UP_HUNT_POINT = REF_GIVE_UP_HUNT_POINT.getDifference(REF_TOP_LEFT_HUNT_POINT)
    private val REF_DELTA_FIRST_FLAG_POINT = REF_FIRST_FLAG_POINT.getDifference(REF_TOP_LEFT_HUNT_POINT)
    private val REF_DELTA_SEARCH_BUTTON_POINT_FROM_LAST_FLAG = REF_SEARCH_POINT.getDifference(REF_SIXTH_FLAG_POINT)
    private val REF_DELTA_FIGHT_BUTTON_POINT = REF_FIGHT_POINT.getDifference(REF_TOP_LEFT_HUNT_POINT)

    private lateinit var giveUpHuntPoint: PointRelative
    private lateinit var searchHuntPoint: PointRelative
    private lateinit var firstFlagPoint: PointRelative
    private lateinit var fightPoint: PointRelative
    private var flagsCount = 0

    fun isHuntPresent(): Boolean {
        val uiPoint = DofusUIPositionsManager.getTreasureHuntUiPosition()
        val topLeftHuntPoint = ConverterUtil.toPointRelative(uiPoint)
        fightPoint = topLeftHuntPoint.getSum(REF_DELTA_FIGHT_BUTTON_POINT)
        val refRect = RectangleRelative(topLeftHuntPoint.x, topLeftHuntPoint.y, 0.1f, 0.1f)
        return ScreenUtil.colorCount(refRect, AppColors.HIGHLIGHT_COLOR_MIN, AppColors.HIGHLIGHT_COLOR_MAX) > 0
            && ScreenUtil.colorCount(refRect, Color(55, 55, 55), Color(60, 60, 60)) > 0
    }

    fun getTreasureHunt(): TreasureHuntMessage {
        return GameInfo.treasureHunt ?: error("No current hunt. Fetch one before executing it")
    }

    fun tickFlag(flagIndex: Int): TreasureHuntMessage {
        val tickPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + FLAG_DELTA_Y * flagIndex)
        MouseUtil.leftClick(tickPoint, false, 0)
        return waitForTreasureHuntUpdate()
    }

    fun getLastNonTickedFlagIndex(): Int? {
        for (i in 0 until flagsCount) {
            val tickPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + FLAG_DELTA_Y * i)
            val tickBox = RectangleRelative(
                tickPoint.x - FLAG_HALF_WIDTH,
                tickPoint.y - FLAG_HALF_WIDTH,
                FLAG_WIDTH,
                FLAG_WIDTH
            )
            if (ScreenUtil.colorCount(tickBox, AppColors.HIGHLIGHT_COLOR_MIN, AppColors.HIGHLIGHT_COLOR_MAX) == 0) {
                return i
            }
        }
        return null
    }

    fun executeStep(step: TreasureHuntStep, logItem: LogItem): DofusMap {
        return when (step) {
            is TreasureHuntStepFollowDirectionToPOI -> ExecutePoiHuntStepTask(step).run(logItem)
            is TreasureHuntStepFollowDirectionToHint -> ExecuteNpcHuntStepTask(step).run(logItem)
            is TreasureHuntStepFight -> ExecuteFightHuntStepTask().run(logItem)
            else -> error("Unsupported hunt step type : [${step::class.java.simpleName}]")
        }
    }

    fun clickSearch() {
        GameInfo.treasureHunt = null
        MouseUtil.leftClick(searchHuntPoint, false, 0)
        waitForTreasureHuntUpdate()
    }

    fun isSearchStep(): Boolean {
        return isHuntPresent() && !isFightStep()
    }

    fun isFightStep(): Boolean {
        return isHuntPresent() && ScreenUtil.isBetween(
            fightPoint,
            AppColors.HIGHLIGHT_COLOR_MIN,
            AppColors.HIGHLIGHT_COLOR_MAX
        )
    }

    fun giveUpHunt(): TreasureHuntMessage {
        GameInfo.treasureHunt = null
        MouseUtil.leftClick(giveUpHuntPoint, false, 0)
        return waitForTreasureHuntUpdate()
    }

    fun fight(logItem: LogItem?) {
        MouseUtil.leftClick(fightPoint, false, 0)
        FightTask().run(logItem)
    }

    private fun waitForTreasureHuntUpdate(): TreasureHuntMessage {
        return WaitUtil.waitForEvents(TreasureHuntMessage::class.java, BasicNoOperationMessage::class.java)
    }

    fun updatePoints() {
        val uiPoint = DofusUIPositionsManager.getTreasureHuntUiPosition()
        val topLeftHuntPoint = ConverterUtil.toPointRelative(uiPoint)
        giveUpHuntPoint = topLeftHuntPoint.getSum(REF_DELTA_GIVE_UP_HUNT_POINT)
        firstFlagPoint = topLeftHuntPoint.getSum(REF_DELTA_FIRST_FLAG_POINT)
        fightPoint = topLeftHuntPoint.getSum(REF_DELTA_FIGHT_BUTTON_POINT)
        if (isSearchStep()) {
            defineFlagsCount()
            val firstFlagPoint = topLeftHuntPoint.getSum(REF_DELTA_FIRST_FLAG_POINT)
            val lastFlagPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + (flagsCount - 1) * FLAG_DELTA_Y)
            searchHuntPoint = lastFlagPoint.getSum(REF_DELTA_SEARCH_BUTTON_POINT_FROM_LAST_FLAG)
        }
    }

    private fun defineFlagsCount() {
        flagsCount = 0
        val x = firstFlagPoint.x + REF_DISPLAY_BORDERS_FROM_FLAG_X
        while (ScreenUtil.colorCount(
                RectangleRelative(
                    x - FLAG_HALF_WIDTH,
                    firstFlagPoint.y + flagsCount * FLAG_DELTA_Y - FLAG_HALF_WIDTH,
                    FLAG_WIDTH,
                    FLAG_WIDTH
                ), AppColors.HIGHLIGHT_COLOR_MIN, AppColors.HIGHLIGHT_COLOR_MAX
            ) == 0
        ) {
            flagsCount++
        }
    }

    fun refreshHuntIfNeeded(logItem: LogItem?) {
        if (isSearchStep()) {
            if (GameInfo.treasureHunt == null) {
                RefreshHuntTask().run(logItem)
            }
            updatePoints()
        }
    }

    fun getLastHintMap(): DofusMap {
        val treasureHunt = getTreasureHunt()
        if (treasureHunt.huntFlags.isEmpty()) {
            return treasureHunt.startMap
        }
        return treasureHunt.huntFlags.last().map
    }
}