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
        private val REF_TELEPORT_LOCATION = PointRelative(0.43902436f, 0.7460087f)
        private val REF_SEARCH_LOCATION = PointRelative(0.6144737f, 0.20559211f)

        private val REF_DELTA_TELEPORT_LOCATION = REF_TELEPORT_LOCATION.getDifference(REF_TOP_LEFT_LOCATION)
        private val REF_DELTA_SEARCH_LOCATION = REF_SEARCH_LOCATION.getDifference(REF_TOP_LEFT_LOCATION)

        private val REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.75259066f, 0.100323625f),
            PointRelative(0.7797927f, 0.13268608f)
        )
    }

    private lateinit var searchLocation: PointRelative
    private lateinit var teleportLocation: PointRelative
    private lateinit var closeZaapSelectionButtonBounds: RectangleRelative

    override fun execute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        if (!ReachHavenBagTask().run(logItem, gameInfo, cancellationToken)) {
            return false
        }
        val playerCellId = gameInfo.entityPositionsOnMapByEntityId[gameInfo.playerId]
            ?: error("Couldn't find player position")

        updateLocations()
        val playerPosition = gameInfo.dofusBoard.getCell(playerCellId)
        val zaapPosition = playerPosition.getCenter().getSum(PointRelative(0f, -4.3f * DofusBoard.TILE_HEIGHT))
        MouseUtil.doubleLeftClick(gameInfo, zaapPosition)
        if (!waitForZaapFrameOpened(gameInfo, cancellationToken)) {
            return false
        }
        val zaapDestMsg = EventStore.getLastEvent(ZaapDestinationsMessage::class.java, gameInfo.snifferId)
            ?: return false

        val zaapDestinations = zaapDestMsg.destinations.map { it.map }
        val zaapDestination = zaapDestinations
            .firstOrNull { it.getCoordinates() == zaap.getCoordinates() }
            ?: error("Could not find zaap destination [${zaap.name}]. Did you explore it with this character ?")

        MouseUtil.leftClick(gameInfo, searchLocation, 1500)
        KeyboardUtil.writeKeyboard(gameInfo, getUniqueIdentifier(zaapDestination, zaapDestinations))
        MouseUtil.leftClick(gameInfo, teleportLocation)
        return MoveUtil.waitForMapChange(gameInfo, cancellationToken)
    }

    private fun waitForZaapFrameOpened(gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        return WaitUtil.waitForSequence(
            gameInfo.snifferId,
            ZaapDestinationsMessage::class.java,
            BasicNoOperationMessage::class.java,
            cancellationToken = cancellationToken
        ) && WaitUtil.waitUntil({ isZaapSelectionOpened(gameInfo) }, cancellationToken)
    }

    private fun isZaapSelectionOpened(gameInfo: GameInfo): Boolean {
        val minColorCross = DofusColors.UI_BANNER_BLACK_COLOR_MIN
        val maxColorCross = DofusColors.UI_BANNER_BLACK_COLOR_MAX
        val minColorBg = DofusColors.UI_BANNER_GREY_COLOR_MIN
        val maxColorBg = DofusColors.UI_BANNER_GREY_COLOR_MAX
        return ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, minColorCross, maxColorCross) > 0
                && ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, minColorBg, maxColorBg) > 0
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

    private fun updateLocations() {
        val zaapUiCenterPoint = DofusUIPositionsManager.getZaapSelectionUiPosition() ?: UIPoint()
        val zaapUiPoint = UIPoint(
            UIBounds.CENTER.x - 750 / 2 + 5 + zaapUiCenterPoint.x,
            UIBounds.CENTER.y - 710 / 2 - 60 + 5 + zaapUiCenterPoint.y
        )
        val zaapUiPointRelative = ConverterUtil.toPointRelative(zaapUiPoint)
        teleportLocation = zaapUiPointRelative.getSum(REF_DELTA_TELEPORT_LOCATION)
        searchLocation = zaapUiPointRelative.getSum(REF_DELTA_SEARCH_LOCATION)
        closeZaapSelectionButtonBounds = REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS
            .getTranslation(REF_TOP_LEFT_LOCATION.opposite())
            .getTranslation(zaapUiPointRelative)
    }

    override fun onStarted(): String {
        val coordinates = zaap.getCoordinates()
        return "Zapping toward [${coordinates.x}, ${coordinates.y}] ..."
    }
}