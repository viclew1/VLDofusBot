package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.interactive.zaap.ZaapDestinationsMessage
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
        RetryUtil.tryUntilSuccess(
            { MouseUtil.leftClick(gameInfo, zaapPosition) },
            { WaitUtil.waitUntil(8000) { getZaapDestMessage(gameInfo) != null } },
            3
        ) ?: error("Didn't receive ZaapDestinationsMessage")

        val zaapDestMsg = getZaapDestMessage(gameInfo)
            ?: error("ZaapDestinationsMessage not found")
        if (!waitForZaapFrameOpened(gameInfo)) {
            error("Couldn't open zaap selection frame")
        }

        return zaapDestMsg.destinations.map { MapManager.getDofusMap(it.mapId) }
    }

    private fun getZaapDestMessage(gameInfo: GameInfo) =
        gameInfo.eventStore.getLastEvent(ZaapDestinationsMessage::class.java)

    private fun waitForZaapFrameOpened(gameInfo: GameInfo): Boolean {
        WaitUtil.waitForEvent(gameInfo, ZaapDestinationsMessage::class.java)
        return WaitUtil.waitUntil { UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.ZAAP_SELECTION) }
    }

    override fun onStarted(): String {
        return "Opening zaap interface ..."
    }
}