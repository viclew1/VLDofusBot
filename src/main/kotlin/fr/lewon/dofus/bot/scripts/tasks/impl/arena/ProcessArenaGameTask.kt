package fr.lewon.dofus.bot.scripts.tasks.impl.arena

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.game.fight.ai.complements.DefaultAIComplement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.arena.GameRolePlayArenaFightPropositionMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil
import java.awt.event.KeyEvent

class ProcessArenaGameTask : BooleanDofusBotTask() {

    companion object {
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
        return UiUtil.isWindowOpenedUsingCloseButton(gameInfo, DofusUIElement.ARENA)
    }

    private fun getFindMatchRectangle(): RectangleRelative {
        return UiUtil.getContainerBounds(DofusUIElement.ARENA, "btn_1v1Queue")
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