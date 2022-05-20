package fr.lewon.dofus.bot.scripts.tasks.impl.arena

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.game.fight.ai.complements.DefaultAIComplement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.arena.GameRolePlayArenaFightPropositionMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.event.KeyEvent

class ProcessArenaGameTask : BooleanDofusBotTask() {

    companion object {
        private val REF_TOP_LEFT_POSITION = PointRelative(0.15953307f, 0.24473257f)
        private val DELTA_TL_FIND_MATCH = PointRelative(0.62f, 0.54f).getDifference(REF_TOP_LEFT_POSITION)
        private val DELTA_BR_FIND_MATCH = PointRelative(0.74f, 0.57f).getDifference(REF_TOP_LEFT_POSITION)

        private val REF_POPUP_POSITION = PointRelative(-0.27110583f, 0.5319465f)
        private val REF_ACCEPT_GAME_BUTTON_RECTANGLE = RectangleRelative.build(
            PointRelative(-0.09274673f, 0.66567606f), PointRelative(0.0035671822f, 0.6879643f)
        )
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val openArenaFrameLogItem = gameInfo.logger.addSubLog("Opening arena frame ...", logItem)
        openArenaFrame(gameInfo)
        gameInfo.logger.closeLog("OK", openArenaFrameLogItem)
        val findingArenaGameLogItem = gameInfo.logger.addSubLog("Finding arena game ...", logItem)
        findingArenaGame(findingArenaGameLogItem, gameInfo)
        gameInfo.logger.closeLog("OK", findingArenaGameLogItem)
        val fightingLogItem = gameInfo.logger.addSubLog("Fighting arena opponent ...", logItem)
        return ArenaFightTask(DefaultAIComplement(idealDist = 0)).run(logItem, gameInfo).also {
            gameInfo.logger.closeLog("OK", fightingLogItem)
        }
    }

    private fun openArenaFrame(gameInfo: GameInfo) {
        KeyboardUtil.sendKey(gameInfo, 'K')
        if (!WaitUtil.waitUntil({ isArenaFrameOpened(gameInfo) })) {
            error("Couldn't open arena frame")
        }
    }

    private fun isArenaFrameOpened(gameInfo: GameInfo): Boolean {
        return ScreenUtil.colorCount(
            gameInfo,
            getFindMatchRectangle(),
            DofusColors.HIGHLIGHT_COLOR_MIN,
            DofusColors.HIGHLIGHT_COLOR_MAX
        ) > 0
    }

    private fun getFindMatchRectangle(): RectangleRelative {
        val arenaFrameLoc = DofusUIElement.ARENA.getPosition()
        val arenaFrameLocPointRelative = ConverterUtil.toPointRelative(arenaFrameLoc)
        val findMatchTopLeft = arenaFrameLocPointRelative.getSum(DELTA_TL_FIND_MATCH)
        val findMatchBottomRight = arenaFrameLocPointRelative.getSum(DELTA_BR_FIND_MATCH)
        return RectangleRelative.build(findMatchTopLeft, findMatchBottomRight)
    }

    private fun findingArenaGame(logItem: LogItem, gameInfo: GameInfo) {
        MouseUtil.leftClick(gameInfo, getFindMatchRectangle().getCenter())
        KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_ESCAPE)
        WaitUtil.waitUntilMessageArrives(
            gameInfo,
            GameRolePlayArenaFightPropositionMessage::class.java,
            6 * 60 * 1000
        )
        gameInfo.logger.addSubLog("Arena game found. Waiting for it to launch ...", logItem)
        launchArenaMatch(logItem, gameInfo)
    }

    private fun launchArenaMatch(logItem: LogItem, gameInfo: GameInfo): Boolean {
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, REF_ACCEPT_GAME_BUTTON_RECTANGLE.getCenter())
        WaitUtil.waitUntilAnyMessageArrives(
            gameInfo,
            GameRolePlayArenaFightPropositionMessage::class.java,
            MapComplementaryInformationsDataMessage::class.java,
            timeout = 6 * 60 * 1000
        )
        return when {
            gameInfo.eventStore.getLastEvent(MapComplementaryInformationsDataMessage::class.java) != null -> {
                gameInfo.logger.addSubLog("Arena game started !", logItem)
                true
            }
            gameInfo.eventStore.getLastEvent(GameRolePlayArenaFightPropositionMessage::class.java) != null -> {
                gameInfo.logger.addSubLog("Arena game found. Waiting for it to launch ...", logItem)
                launchArenaMatch(logItem, gameInfo)
            }
            else -> error("Did not receive any expected message")
        }
    }

    override fun onStarted(): String {
        return "Processing an arena single fight ..."
    }

}