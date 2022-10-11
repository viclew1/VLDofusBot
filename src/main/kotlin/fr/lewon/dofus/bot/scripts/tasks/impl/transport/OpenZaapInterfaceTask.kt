package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class OpenZaapInterfaceTask : DofusBotTask<List<DofusMap>>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo): List<DofusMap> {
        if (!ReachHavenBagTask().run(logItem, gameInfo)) {
            error("Couldn't reach haven bag")
        }

        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player position")

        WaitUtil.sleep(500)
        val playerPosition = gameInfo.dofusBoard.getCell(playerCellId)
        val zaapPosition = playerPosition.getCenter().getSum(PointRelative(0f, -4.3f * DofusBoard.TILE_HEIGHT))
        gameInfo.eventStore.clear()
        val getZaapDestMessageFun = { gameInfo.eventStore.getLastEvent(ZaapDestinationsMessage::class.java) }
        RetryUtil.tryUntilSuccess(
            { MouseUtil.leftClick(gameInfo, zaapPosition) },
            { WaitUtil.waitUntil({ getZaapDestMessageFun() != null }, 8000) },
            3
        ) ?: error("Couldn't open zaap selection frame")
        if (!waitForZaapFrameOpened(gameInfo)) {
            error("Couldn't open zaap selection frame")
        }
        val zaapDestMsg = getZaapDestMessageFun()
            ?: error("Zaap destinations not found")

        return zaapDestMsg.destinations.map { it.map }
    }

    private fun waitForZaapFrameOpened(gameInfo: GameInfo): Boolean {
        WaitUtil.waitForEvents(gameInfo, ZaapDestinationsMessage::class.java, BasicNoOperationMessage::class.java)
        return WaitUtil.waitUntil({ UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.ZAAP_SELECTION) })
    }

    override fun onStarted(): String {
        return "Opening zaap interface ..."
    }
}