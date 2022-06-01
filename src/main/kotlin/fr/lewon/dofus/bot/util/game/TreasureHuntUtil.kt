package fr.lewon.dofus.bot.util.game

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.game.fight.ai.complements.TreasureChestAIComplement
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
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil
import java.awt.Color

object TreasureHuntUtil {

    private fun getRefStepBounds(): RectangleRelative {
        return UiUtil.getContainerBounds(DofusUIElement.TREASURE_HUNT, "ctr_step")
    }

    private fun getRefFlagBounds(): RectangleRelative {
        return UiUtil.getContainerBounds(DofusUIElement.TREASURE_HUNT, "tx_flag")
    }

    private fun getRefSearchPoint(): PointRelative {
        return UiUtil.getContainerBounds(DofusUIElement.TREASURE_HUNT, "btn_dig").getCenter()
    }

    private fun getRefFightBounds(): PointRelative {
        return UiUtil.getContainerBounds(DofusUIElement.TREASURE_HUNT, "btn_digFight").getCenter()
    }

    private fun getFightPoint(): PointRelative {
        return getRefFightBounds().getSum(PointRelative(0f, getRefStepBounds().height))
    }

    private fun getGiveUpHuntPoint(): PointRelative {
        return UiUtil.getContainerBounds(DofusUIElement.TREASURE_HUNT, "btn_giveUp").getCenter()
    }

    private fun getSearchHuntPoint(gameInfo: GameInfo): PointRelative {
        val dy = (getTreasureHunt(gameInfo).totalStepCount + 1) * getRefStepBounds().height
        return getRefSearchPoint().getSum(PointRelative(0f, dy))
    }

    fun isHuntPresent(gameInfo: GameInfo): Boolean {
        val arrowMinimizeRect = UiUtil.getContainerBounds(DofusUIElement.TREASURE_HUNT, "btn_arrowMinimize")
        return ScreenUtil.colorCount(
            gameInfo, arrowMinimizeRect, DofusColors.HIGHLIGHT_COLOR_MIN, DofusColors.HIGHLIGHT_COLOR_MAX
        ) > 0 && ScreenUtil.colorCount(gameInfo, arrowMinimizeRect, Color(55, 55, 55), Color(60, 60, 60)) > 0
    }

    fun getTreasureHunt(gameInfo: GameInfo): TreasureHuntMessage {
        return gameInfo.treasureHunt ?: error("No current hunt. Fetch one before executing it")
    }

    private fun getFlagBounds(flagIndex: Int): RectangleRelative {
        return getRefFlagBounds().getTranslation(PointRelative(0f, (flagIndex + 1) * getRefStepBounds().height))
    }

    fun tickFlag(gameInfo: GameInfo, flagIndex: Int) {
        val flagPoint = getFlagBounds(flagIndex).getCenter()
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, flagPoint)
        waitForTreasureHuntUpdate(gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
    }

    fun getLastNonTickedFlagIndex(gameInfo: GameInfo): Int? {
        val minColor = DofusColors.HIGHLIGHT_COLOR_MIN
        val maxColor = DofusColors.HIGHLIGHT_COLOR_MAX
        for (i in 0 until getFlagsCount(gameInfo)) {
            val tickBox = getFlagBounds(i)
            if (ScreenUtil.colorCount(gameInfo, tickBox, minColor, maxColor) == 0) {
                return i
            }
        }
        return null
    }

    fun executeStep(
        gameInfo: GameInfo,
        step: TreasureHuntStep,
        logItem: LogItem,
    ): Boolean {
        return when (step) {
            is TreasureHuntStepFollowDirectionToPOI ->
                ExecutePoiHuntStepTask(step).run(logItem, gameInfo)
            is TreasureHuntStepFollowDirectionToHint ->
                ExecuteNpcHuntStepTask(step).run(logItem, gameInfo)
            is TreasureHuntStepFight ->
                ExecuteFightHuntStepTask().run(logItem, gameInfo)
            else -> error("Unsupported hunt step type : [${step::class.java.simpleName}]")
        }
    }

    fun clickSearch(gameInfo: GameInfo) {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, getSearchHuntPoint(gameInfo))
        waitForTreasureHuntUpdate(gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
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
            getFightPoint(),
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        )
    }

    fun giveUpHunt(gameInfo: GameInfo) {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, getGiveUpHuntPoint())
        waitForTreasureHuntUpdate(gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
    }

    fun fight(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, getFightPoint())
        return FightTask(TreasureChestAIComplement()).run(logItem, gameInfo)
    }

    fun clickFightForUpdate(gameInfo: GameInfo) {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, getFightPoint())
        waitForTreasureHuntUpdate(gameInfo)
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
    }

    fun waitForTreasureHuntUpdate(gameInfo: GameInfo) {
        WaitUtil.waitForEvents(gameInfo, TreasureHuntMessage::class.java, BasicNoOperationMessage::class.java)
        if (!WaitUtil.waitUntil({ isHuntPresent(gameInfo) })) {
            error("Can't find treasure hunt frame. Hunt most likely failed.")
        }
    }

    fun getFlagsCount(gameInfo: GameInfo): Int {
        val minColor = Color(150, 125, 80)
        val maxColor = Color(200, 170, 120)
        var flagIndex = 0
        while (ScreenUtil.colorCount(gameInfo, getFlagBounds(flagIndex), minColor, maxColor) != 0) {
            if (flagIndex++ > 20) {
                error("Invalid flag count.")
            }
        }
        return flagIndex
    }

    fun getLastHintMap(gameInfo: GameInfo): DofusMap {
        val treasureHunt = getTreasureHunt(gameInfo)
        if (treasureHunt.huntFlags.isEmpty()) {
            return treasureHunt.startMap
        }
        return treasureHunt.huntFlags.last().map
    }
}