package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.impl.fight.FightTask
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
import fr.lewon.dofus.bot.util.network.GameInfo
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

    private lateinit var topLeftHuntPoint: PointRelative
    private lateinit var giveUpHuntPoint: PointRelative
    private lateinit var searchHuntPoint: PointRelative
    private lateinit var firstFlagPoint: PointRelative
    private lateinit var fightPoint: PointRelative
    var flagsCount = 0

    private fun getTreasureHuntUiPosition(): UIPoint {
        return DofusUIPositionsManager.getTreasureHuntUiPosition() ?: DefaultUIPositions.TREASURE_HUNT_UI_POSITION
    }

    fun isHuntPresent(gameInfo: GameInfo): Boolean {
        val uiPoint = getTreasureHuntUiPosition()
        topLeftHuntPoint = ConverterUtil.toPointRelative(uiPoint)
        val refRect = RectangleRelative(topLeftHuntPoint.x, topLeftHuntPoint.y, 0.1f, 0.1f)
        val isHuntPresent =
            ScreenUtil.colorCount(
                gameInfo, refRect, DofusColors.HIGHLIGHT_COLOR_MIN, DofusColors.HIGHLIGHT_COLOR_MAX
            ) > 0 && ScreenUtil.colorCount(gameInfo, refRect, Color(55, 55, 55), Color(60, 60, 60)) > 0
        if (isHuntPresent) {
            updatePoints(gameInfo)
        }
        return isHuntPresent
    }

    fun getTreasureHunt(gameInfo: GameInfo): TreasureHuntMessage {
        return gameInfo.treasureHunt ?: error("No current hunt. Fetch one before executing it")
    }

    fun tickFlag(gameInfo: GameInfo, flagIndex: Int, cancellationToken: CancellationToken): Boolean {
        val tickPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + FLAG_DELTA_Y * flagIndex)
        MouseUtil.leftClick(gameInfo, tickPoint)
        return waitForTreasureHuntUpdate(gameInfo, cancellationToken)
    }

    fun getLastNonTickedFlagIndex(gameInfo: GameInfo): Int? {
        for (i in 0 until flagsCount) {
            val tickPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + FLAG_DELTA_Y * i)
            val tickBox = RectangleRelative(
                tickPoint.x - FLAG_HALF_WIDTH,
                tickPoint.y - FLAG_HALF_WIDTH,
                FLAG_WIDTH,
                FLAG_WIDTH
            )
            if (ScreenUtil.colorCount(
                    gameInfo,
                    tickBox,
                    DofusColors.HIGHLIGHT_COLOR_MIN,
                    DofusColors.HIGHLIGHT_COLOR_MAX
                ) == 0
            ) {
                return i
            }
        }
        return null
    }

    fun executeStep(
        gameInfo: GameInfo,
        step: TreasureHuntStep,
        logItem: LogItem,
        cancellationToken: CancellationToken
    ): Boolean {
        return when (step) {
            is TreasureHuntStepFollowDirectionToPOI ->
                ExecutePoiHuntStepTask(step).run(logItem, gameInfo, cancellationToken)
            is TreasureHuntStepFollowDirectionToHint ->
                ExecuteNpcHuntStepTask(step).run(logItem, gameInfo, cancellationToken)
            is TreasureHuntStepFight ->
                ExecuteFightHuntStepTask().run(logItem, gameInfo, cancellationToken)
            else -> error("Unsupported hunt step type : [${step::class.java.simpleName}]")
        }
    }

    fun clickSearch(gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, searchHuntPoint)
        return waitForTreasureHuntUpdate(gameInfo, cancellationToken)
    }

    fun isSearchStep(gameInfo: GameInfo): Boolean {
        return isHuntPresent(gameInfo) && !isFightStep(gameInfo)
    }

    fun isFightStep(gameInfo: GameInfo): Boolean {
        return isHuntPresent(gameInfo) && isFightButtonPresent(gameInfo)
    }

    private fun isFightButtonPresent(gameInfo: GameInfo): Boolean {
        return ScreenUtil.isBetween(
            gameInfo,
            fightPoint,
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        )
    }

    fun giveUpHunt(gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, giveUpHuntPoint)
        return waitForTreasureHuntUpdate(gameInfo, cancellationToken)
    }

    fun fight(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        MouseUtil.leftClick(gameInfo, fightPoint)
        return FightTask().run(logItem, gameInfo, cancellationToken)
    }

    fun waitForTreasureHuntUpdate(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken
    ): Boolean {
        return WaitUtil.waitForSequence(
            gameInfo.snifferId,
            TreasureHuntMessage::class.java,
            BasicNoOperationMessage::class.java,
            cancellationToken = cancellationToken
        ) && WaitUtil.waitUntil({ isHuntPresent(gameInfo) }, cancellationToken)
    }

    private fun updatePoints(gameInfo: GameInfo) {
        giveUpHuntPoint = topLeftHuntPoint.getSum(REF_DELTA_GIVE_UP_HUNT_POINT)
        firstFlagPoint = topLeftHuntPoint.getSum(REF_DELTA_FIRST_FLAG_POINT)
        fightPoint = topLeftHuntPoint.getSum(REF_DELTA_FIGHT_BUTTON_POINT)
        if (!isFightButtonPresent(gameInfo)) {
            defineFlagsCount(gameInfo)
            val firstFlagPoint = topLeftHuntPoint.getSum(REF_DELTA_FIRST_FLAG_POINT)
            val lastFlagPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + (flagsCount - 1) * FLAG_DELTA_Y)
            searchHuntPoint = lastFlagPoint.getSum(REF_DELTA_SEARCH_BUTTON_POINT_FROM_LAST_FLAG)
        }
    }

    private fun defineFlagsCount(gameInfo: GameInfo) {
        flagsCount = 0
        val x = firstFlagPoint.x + REF_DISPLAY_BORDERS_FROM_FLAG_X
        while (ScreenUtil.colorCount(
                gameInfo,
                RectangleRelative(
                    x - FLAG_HALF_WIDTH,
                    firstFlagPoint.y + flagsCount * FLAG_DELTA_Y - FLAG_HALF_WIDTH,
                    FLAG_WIDTH,
                    FLAG_WIDTH
                ), DofusColors.HIGHLIGHT_COLOR_MIN, DofusColors.HIGHLIGHT_COLOR_MAX
            ) == 0
        ) {
            flagsCount++
        }
    }

    fun getLastHintMap(gameInfo: GameInfo): DofusMap {
        val treasureHunt = getTreasureHunt(gameInfo)
        if (treasureHunt.huntFlags.isEmpty()) {
            return treasureHunt.startMap
        }
        return treasureHunt.huntFlags.last().map
    }
}