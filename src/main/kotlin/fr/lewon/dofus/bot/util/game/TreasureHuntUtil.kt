package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step.ExecuteNpcHuntStepTask
import fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step.ExecutePoiHuntStepTask
import fr.lewon.dofus.bot.sniffer.model.messages.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStep
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.imagetreatment.MatManager
import fr.lewon.dofus.bot.util.imagetreatment.OpenCvUtil
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import java.awt.Rectangle

object TreasureHuntUtil {

    private val REF_TOP_RIGHT_HUNT_POINT = PointRelative(0.0062942565f, 0.17010815f)
    private val REF_GIVE_UP_HUNT_POINT = PointRelative(-0.010126582f, 0.21835445f)
    private val REF_SEARCH_HUNT_POINT = PointRelative(-0.024705883f, 0.4647059f)
    private val REF_FIRST_FLAG_POINT = PointRelative(-0.014117647f, 0.2882353f)
    private val REF_SIXTH_FLAG_POINT = PointRelative(-0.014117647f, 0.42794117f)

    private val FLAG_DELTA_Y = (REF_SIXTH_FLAG_POINT.y - REF_FIRST_FLAG_POINT.y) / 5

    private val REF_DELTA_GIVE_UP_HUNT_POINT = PointRelative(
        REF_GIVE_UP_HUNT_POINT.x - REF_TOP_RIGHT_HUNT_POINT.x, REF_GIVE_UP_HUNT_POINT.y - REF_TOP_RIGHT_HUNT_POINT.y
    )
    private val REF_DELTA_SEARCH_BUTTON_POINT = PointRelative(
        REF_SEARCH_HUNT_POINT.x - REF_TOP_RIGHT_HUNT_POINT.x, REF_SEARCH_HUNT_POINT.y - REF_TOP_RIGHT_HUNT_POINT.y
    )
    private val REF_DELTA_FIRST_FLAG_POINT = PointRelative(
        REF_FIRST_FLAG_POINT.x - REF_TOP_RIGHT_HUNT_POINT.x, REF_FIRST_FLAG_POINT.y - REF_TOP_RIGHT_HUNT_POINT.y
    )

    private lateinit var giveUpHuntPoint: PointRelative
    private lateinit var searchHuntPoint: PointRelative
    private lateinit var firstFlagPoint: PointRelative

    fun isHuntPresent(): Boolean {
        return getTreasureHuntBounds() != null
    }

    fun getTreasureHunt(): TreasureHuntMessage {
        return GameInfo.treasureHunt ?: error("No current hunt. Fetch one before executing it")
    }

    fun tickFlag(flagIndex: Int): TreasureHuntMessage {
        updatePoints()
        val tickPoint = PointRelative(firstFlagPoint.x, firstFlagPoint.y + FLAG_DELTA_Y * flagIndex)
        MouseUtil.leftClick(tickPoint, false, 0)
        return waitForTreasureHuntUpdate()
    }

    fun executeStep(step: TreasureHuntStep, logItem: LogItem): DofusMap {
        updatePoints()
        if (step is TreasureHuntStepFollowDirectionToPOI) {
            return ExecutePoiHuntStepTask(step).run(logItem)
        } else if (step is TreasureHuntStepFollowDirectionToHint) {
            return ExecuteNpcHuntStepTask(step).run(logItem)
        }
        error("Unsupported hunt step type : [${step::class.java.simpleName}]")
    }

    fun clickSearch(): TreasureHuntMessage {
        updatePoints()
        MouseUtil.leftClick(searchHuntPoint, false, 0)
        return waitForTreasureHuntUpdate()
    }

    fun giveUpHunt(): TreasureHuntMessage {
        updatePoints()
        MouseUtil.leftClick(giveUpHuntPoint, false, 0)
        return waitForTreasureHuntUpdate()
    }

    private fun waitForTreasureHuntUpdate(): TreasureHuntMessage {
        return EventStore.waitForEvent(TreasureHuntMessage::class.java)
    }

    private fun getTreasureHuntBounds(): Rectangle? {
        return OpenCvUtil.getPatternBounds(MatManager.TOP_HUNT_MAT.buildMat(), 0.6)
    }

    private fun updatePoints() {
        val bounds = getTreasureHuntBounds() ?: error("No hunt present")
        val topRightHuntPoint = ConverterUtil.toPointRelative(PointAbsolute(bounds.x + bounds.width, bounds.y))
        giveUpHuntPoint = PointRelative(
            topRightHuntPoint.x + REF_DELTA_GIVE_UP_HUNT_POINT.x, topRightHuntPoint.y + REF_DELTA_GIVE_UP_HUNT_POINT.y
        )
        searchHuntPoint = PointRelative(
            topRightHuntPoint.x + REF_DELTA_SEARCH_BUTTON_POINT.x, topRightHuntPoint.y + REF_DELTA_SEARCH_BUTTON_POINT.y
        )
        firstFlagPoint = PointRelative(
            topRightHuntPoint.x + REF_DELTA_FIRST_FLAG_POINT.x, topRightHuntPoint.y + REF_DELTA_FIRST_FLAG_POINT.y
        )
    }
}