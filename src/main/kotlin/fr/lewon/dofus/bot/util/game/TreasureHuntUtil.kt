package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.logs.LogItem
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
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.imagetreatment.MatManager
import fr.lewon.dofus.bot.util.imagetreatment.OpenCvUtil
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

object TreasureHuntUtil {

    private val REF_TOP_RIGHT_HUNT_POINT = PointRelative(0.0062942565f, 0.17010815f)
    private val REF_GIVE_UP_HUNT_POINT = PointRelative(-0.010126582f, 0.21835445f)
    private val REF_FIGHT_POINT = PointRelative(-0.08969315f, 0.2910521f)
    private val REF_FIRST_FLAG_POINT = PointRelative(-0.014117647f, 0.2882353f)
    private val REF_SIXTH_FLAG_POINT = PointRelative(-0.014117647f, 0.42794117f)

    private const val FLAG_WIDTH = 0.02f
    private const val FLAG_HALF_WIDTH = FLAG_WIDTH / 2f
    private val FLAG_DELTA_Y = (REF_SIXTH_FLAG_POINT.y - REF_FIRST_FLAG_POINT.y) / 5

    private val REF_DELTA_GIVE_UP_HUNT_POINT = REF_GIVE_UP_HUNT_POINT.getDifference(REF_TOP_RIGHT_HUNT_POINT)
    private val REF_DELTA_SEARCH_BUTTON_POINT_FROM_BOT_RIGHT = PointRelative(-0.03f, -0.02f)
    private val REF_DELTA_FLAGS_AREA_LIMIT_POINT_FROM_BOT_RIGHT = PointRelative(0f, -0.04f)
    private val REF_DELTA_FIGHT_BUTTON_POINT = REF_FIGHT_POINT.getDifference(REF_TOP_RIGHT_HUNT_POINT)
    private val REF_DELTA_FIRST_FLAG_POINT = REF_FIRST_FLAG_POINT.getDifference(REF_TOP_RIGHT_HUNT_POINT)

    private lateinit var giveUpHuntPoint: PointRelative
    private lateinit var searchHuntPoint: PointRelative
    private lateinit var firstFlagPoint: PointRelative
    private lateinit var flagsAreaLimitPoint: PointRelative

    fun isHuntPresent(): Boolean {
        return isSearchStep() || isFightStep()
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
        val flagsLimitY = flagsAreaLimitPoint.y
        for (i in 0 until 20) {
            val tickPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + FLAG_DELTA_Y * i)
            if (tickPoint.y > flagsLimitY) {
                return null
            }
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
        return getSearchStepBounds() != null
    }

    fun isFightStep(): Boolean {
        return getFightStepBounds() != null
    }

    fun giveUpHunt(): TreasureHuntMessage {
        GameInfo.treasureHunt = null
        MouseUtil.leftClick(giveUpHuntPoint, false, 0)
        return waitForTreasureHuntUpdate()
    }

    fun fight(logItem: LogItem?) {
        val fightBounds = getFightStepBounds() ?: error("Current step isn't fight step")
        val topRightHuntPoint = ConverterUtil.toPointRelative(fightBounds.getTopRight())
        val fightPoint = topRightHuntPoint.getSum(REF_DELTA_FIGHT_BUTTON_POINT)
        MouseUtil.leftClick(fightPoint, false, 0)
        FightTask().run(logItem)
    }

    private fun waitForTreasureHuntUpdate(): TreasureHuntMessage {
        return WaitUtil.waitForEvents(TreasureHuntMessage::class.java, BasicNoOperationMessage::class.java)
    }

    private fun getSearchStepBounds(): RectangleAbsolute? {
        return OpenCvUtil.getFrameBounds(
            MatManager.TOP_HUNT_MAT.buildMat(),
            MatManager.BOT_HUNT_MAT.buildMat(),
            MatManager.LEFT_HUNT_MAT.buildMat(),
            MatManager.RIGHT_HUNT_MAT.buildMat(),
            0.5
        )
    }

    private fun getFightStepBounds(): RectangleAbsolute? {
        return OpenCvUtil.getFrameBounds(
            MatManager.TOP_HUNT_MAT.buildMat(),
            MatManager.BOT_FIGHT_HUNT_MAT.buildMat(),
            MatManager.LEFT_HUNT_MAT.buildMat(),
            MatManager.RIGHT_HUNT_MAT.buildMat(),
            0.7
        )
    }

    fun updatePoints() {
        val bounds = getSearchStepBounds() ?: error("No hunt present")
        val topRightHuntPoint = ConverterUtil.toPointRelative(bounds.getTopRight())
        val botRightHuntPoint = ConverterUtil.toPointRelative(bounds.getBottomRight())
        giveUpHuntPoint = topRightHuntPoint.getSum(REF_DELTA_GIVE_UP_HUNT_POINT)
        firstFlagPoint = topRightHuntPoint.getSum(REF_DELTA_FIRST_FLAG_POINT)
        searchHuntPoint = botRightHuntPoint.getSum(REF_DELTA_SEARCH_BUTTON_POINT_FROM_BOT_RIGHT)
        flagsAreaLimitPoint = botRightHuntPoint.getSum(REF_DELTA_FLAGS_AREA_LIMIT_POINT_FROM_BOT_RIGHT)
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