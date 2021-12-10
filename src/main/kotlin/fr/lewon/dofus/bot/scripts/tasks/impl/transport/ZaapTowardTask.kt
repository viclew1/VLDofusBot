package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.manager.ui.UIBounds
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.game.move.transporters.Zaap
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.GameInfo

class ZaapTowardTask(private val zaap: Zaap) : BooleanDofusBotTask() {

    companion object {
        private val REF_TOP_LEFT_LOCATION = PointRelative(0.21373057f, 0.10194175f)


        private val REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.75259066f, 0.100323625f),
            PointRelative(0.7797927f, 0.13268608f)
        )

        val MIN_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MIN
        val MAX_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MAX
        val MIN_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MIN
        val MAX_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MAX
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (!ReachHavenBagTask().run(logItem, gameInfo, cancellationToken)) {
            return false
        }

        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player position")

        val playerPosition = gameInfo.dofusBoard.getCell(playerCellId)
        val zaapPosition = playerPosition.getCenter().getSum(PointRelative(0f, -4.3f * DofusBoard.TILE_HEIGHT))
        EventStore.clear(gameInfo.snifferId)
        MouseUtil.leftClick(gameInfo, zaapPosition)
        if (!waitForZaapFrameOpened(gameInfo, cancellationToken)) {
            error("Couldn't open zaap selection frame")
        }
        val zaapDestMsg = EventStore.getLastEvent(ZaapDestinationsMessage::class.java, gameInfo.snifferId)
            ?: error("Zaap destinations not found")

        val zaapDestinations = zaapDestMsg.destinations.map { it.map }
        val zaapDestination = zaapDestinations
            .firstOrNull { it.getCoordinates() == zaap.getCoordinates() }
            ?: error("Could not find zaap destination [${zaap.name}]. Did you explore it with this character ?")

        KeyboardUtil.writeKeyboard(gameInfo, getUniqueIdentifier(zaapDestination, zaapDestinations), 100)
        KeyboardUtil.enter(gameInfo)
        EventStore.clear(gameInfo.snifferId)
        MoveUtil.waitForMapChange(gameInfo, cancellationToken)
        return true
    }

    private fun waitForZaapFrameOpened(
        gameInfo: GameInfo,
        cancellationToken: CancellationToken
    ): Boolean {
        WaitUtil.waitForEvents(
            gameInfo.snifferId,
            ZaapDestinationsMessage::class.java,
            BasicNoOperationMessage::class.java,
            cancellationToken = cancellationToken,
            removeWhenFound = false
        )
        return WaitUtil.waitUntil({ isZaapFrameOpened(gameInfo) }, cancellationToken)
    }

    private fun isZaapFrameOpened(gameInfo: GameInfo): Boolean {
        val closeZaapSelectionButtonBounds = getCloseZaapSelectionButtonBounds()
        return ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
                && ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, MIN_COLOR_BG, MAX_COLOR_BG) > 0
    }

    private fun getUniqueIdentifier(destMap: DofusMap, destinations: List<DofusMap>): String {
        val destSubAreaLabel = destMap.getSubAreaLabel()
        val destinationsMatchingSubAreaLabel = destinations.filter { it.getSubAreaLabel() == destSubAreaLabel }
        if (destinationsMatchingSubAreaLabel.size == 1) return destSubAreaLabel

        val destAreaLabel = destMap.getAreaLabel()
        val destinationsMatchingAreaLabel = destinations.filter { it.getAreaLabel() == destAreaLabel }
        if (destinationsMatchingAreaLabel.size == 1) return destAreaLabel

        val coordinates = destMap.getCoordinates()
        error("No unique identifier for destination [${coordinates.x}, ${coordinates.y}]")
    }

    private fun getCloseZaapSelectionButtonBounds(): RectangleRelative {
        val zaapUiCenterPoint = DofusUIPositionsManager.getZaapSelectionUiPosition() ?: UIPoint()
        val zaapUiPoint = UIPoint(
            UIBounds.CENTER.x - 750 / 2 + 5 + zaapUiCenterPoint.x,
            UIBounds.CENTER.y - 710 / 2 - 60 + 5 + zaapUiCenterPoint.y
        )
        val zaapUiPointRelative = ConverterUtil.toPointRelative(zaapUiPoint)
        return REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS
            .getTranslation(REF_TOP_LEFT_LOCATION.opposite())
            .getTranslation(zaapUiPointRelative)
    }

    override fun onStarted(): String {
        val coordinates = zaap.getCoordinates()
        return "Zapping toward [${coordinates.x}, ${coordinates.y}] ..."
    }
}