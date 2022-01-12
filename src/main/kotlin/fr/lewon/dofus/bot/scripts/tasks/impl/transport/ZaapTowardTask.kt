package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.dat.managers.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.manager.dat.managers.TransportSortingUtil
import fr.lewon.dofus.bot.core.manager.ui.UIBounds
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.ZaapDestinationsMessage
import fr.lewon.dofus.bot.util.game.DofusColors
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.RetryUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.event.KeyEvent
import java.text.Normalizer

class ZaapTowardTask(private val zaap: DofusMap) : BooleanDofusBotTask() {

    companion object {
        private val REF_TOP_LEFT_LOCATION = PointRelative(0.21373057f, 0.10194175f)

        private val REF_HEADER_REGION_BUTTON = PointRelative(0.4804992f, 0.25730994f)

        private val REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.75259066f, 0.100323625f),
            PointRelative(0.7797927f, 0.13268608f)
        )

        private val REF_FIRST_ELEMENT_LOCATION = PointRelative(0.43837753f, 0.2962963f)
        private val REF_TENTH_ELEMENT_LOCATION = PointRelative(0.43837753f, 0.64912283f)
        private val DELTA_ELEMENT = (REF_TENTH_ELEMENT_LOCATION.y - REF_FIRST_ELEMENT_LOCATION.y) / 9f

        val MIN_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MIN
        val MAX_COLOR_CROSS = DofusColors.UI_BANNER_BLACK_COLOR_MAX
        val MIN_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MIN
        val MAX_COLOR_BG = DofusColors.UI_BANNER_GREY_COLOR_MAX
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (!ReachHavenBagTask().run(logItem, gameInfo)) {
            return false
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

        val zaapDestinations = zaapDestMsg.destinations.map { it.map }
        val zaapDestination = zaapDestinations
            .firstOrNull { it.getCoordinates() == zaap.getCoordinates() }
            ?: error("Could not find zaap destination [${zaap.getCoordinates().x} ; ${zaap.getCoordinates().y}]. Did you explore it with this character ?")

        focusZaap(gameInfo, zaapDestination, zaapDestinations)
        gameInfo.eventStore.clear()
        KeyboardUtil.enter(gameInfo)
        MoveUtil.waitForMapChange(gameInfo)
        return true
    }

    private fun focusZaap(gameInfo: GameInfo, zaapDestination: DofusMap, zaapDestinations: List<DofusMap>) {
        val sortingMode = TransportSortingUtil.getZaapSortingMode()
        val regionButtonLocation = getHeaderRegionButtonLocation()
        if (sortingMode.sortCriteria != "areaName") {
            MouseUtil.leftClick(gameInfo, regionButtonLocation, 500)
        }
        if (sortingMode.descendingSort) {
            MouseUtil.leftClick(gameInfo, regionButtonLocation, 500)
        }
        val orderedZaaps = getOrderedZaapDestinations(zaapDestinations)
        val zaapDestinationIndex = orderedZaaps.indexOf(zaapDestination)
        val firstElementLocation = getFirstElementLocation()
        val tenthElementLocation = getTenthElementLocation()
        MouseUtil.leftClick(gameInfo, firstElementLocation, 200)
        var skippedCount = 0
        while (zaapDestinationIndex - skippedCount >= 10) {
            MouseUtil.leftClick(gameInfo, tenthElementLocation)
            KeyboardUtil.sendKey(gameInfo, KeyEvent.VK_DOWN, 200)
            skippedCount += minOf(10, zaapDestinations.size - 10 - skippedCount)
        }
        val zaapLocation = firstElementLocation.also { it.y += DELTA_ELEMENT * (zaapDestinationIndex - skippedCount) }
        MouseUtil.leftClick(gameInfo, zaapLocation)
    }

    private fun getOrderedZaapDestinations(zaapDestinations: List<DofusMap>): List<DofusMap> {
        val favoriteZaaps = TransportSortingUtil.getFavoriteZaapMapIds().map { it.toInt() }
        val favoriteIndexFunc: (DofusMap) -> String = { if (favoriteZaaps.contains(it.id.toInt())) "A" else "B" }
        return zaapDestinations.sortedBy { removeAccents(favoriteIndexFunc(it) + it.subArea.area.name + it.subArea.name) }
    }

    private fun removeAccents(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return regex.replace(temp, "")
    }

    private fun waitForZaapFrameOpened(gameInfo: GameInfo): Boolean {
        WaitUtil.waitForEvents(
            gameInfo,
            ZaapDestinationsMessage::class.java,
            BasicNoOperationMessage::class.java,
        )
        return WaitUtil.waitUntil({ isZaapFrameOpened(gameInfo) })
    }

    private fun isZaapFrameOpened(gameInfo: GameInfo): Boolean {
        val closeZaapSelectionButtonBounds = getCloseZaapSelectionButtonBounds()
        return ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, MIN_COLOR_CROSS, MAX_COLOR_CROSS) > 0
                && ScreenUtil.colorCount(gameInfo, closeZaapSelectionButtonBounds, MIN_COLOR_BG, MAX_COLOR_BG) > 0
    }

    private fun getCloseZaapSelectionButtonBounds(): RectangleRelative {
        return REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS
            .getTranslation(REF_TOP_LEFT_LOCATION.opposite())
            .getTranslation(getZaapTopLeftLocation())
    }

    private fun getHeaderRegionButtonLocation(): PointRelative {
        return REF_HEADER_REGION_BUTTON
            .getDifference(REF_TOP_LEFT_LOCATION)
            .getSum(getZaapTopLeftLocation())
    }

    private fun getFirstElementLocation(): PointRelative {
        return REF_FIRST_ELEMENT_LOCATION
            .getDifference(REF_TOP_LEFT_LOCATION)
            .getSum(getZaapTopLeftLocation())
    }

    private fun getTenthElementLocation(): PointRelative {
        return REF_TENTH_ELEMENT_LOCATION
            .getDifference(REF_TOP_LEFT_LOCATION)
            .getSum(getZaapTopLeftLocation())
    }

    private fun getZaapTopLeftLocation(): PointRelative {
        val zaapUiCenterPoint = DofusUIPositionsManager.getZaapSelectionUiPosition() ?: UIPoint()
        val zaapUiPoint = UIPoint(
            UIBounds.CENTER.x - 750 / 2 + 5 + zaapUiCenterPoint.x,
            UIBounds.CENTER.y - 710 / 2 - 60 + 5 + zaapUiCenterPoint.y
        )
        return ConverterUtil.toPointRelative(zaapUiPoint)
    }

    override fun onStarted(): String {
        val coordinates = zaap.getCoordinates()
        return "Zapping toward [${coordinates.x}, ${coordinates.y}] ..."
    }
}