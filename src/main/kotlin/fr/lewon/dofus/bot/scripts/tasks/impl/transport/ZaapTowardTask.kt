package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.core.ui.managers.TransportSortingUtil
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.StringUtil
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class ZaapTowardTask(private val zaap: DofusMap) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val zaapDestinations = OpenZaapInterfaceTask().run(logItem, gameInfo)
        if (!zaapDestinations.contains(zaap)) {
            error("Could not find zaap destination [${zaap.getCoordinates().x};${zaap.getCoordinates().y}]. Did you explore it with this character ?")
        }
        val filteredDestinations = getFilteredZaapDestinations(gameInfo, zaapDestinations)
        zaapToDestination(gameInfo, filteredDestinations)
        if (zaap.id != gameInfo.currentMap.id) {
            error("Did not reach expected map : [${zaap.getCoordinates().x};${zaap.getCoordinates().y}]")
        }
        return true
    }

    private fun getFilteredZaapDestinations(gameInfo: GameInfo, zaapDestinations: List<DofusMap>): List<DofusMap> {
        val sortedZaapDestinations = getOrderedZaapDestinations(zaapDestinations)
        val zaapBounds = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "gd_zaap")
        val totalElementsHeight = zaapBounds.height
        val zaapIndex = sortedZaapDestinations.indexOf(zaap)
        return if (zaapIndex < 10) {
            sortedZaapDestinations
        } else if (zaapIndex >= sortedZaapDestinations.size - 10) {
            forceScroll(gameInfo, zaapBounds.getBottomRight().getDifference(PointRelative(0.001f, 0.001f)))
            sortedZaapDestinations.subList(sortedZaapDestinations.size - 10, sortedZaapDestinations.size)
        } else {
            val scrollbarHeightPerItem = totalElementsHeight / zaapDestinations.size
            val scrollPoint = zaapBounds.getTopRight()
                .getSum(PointRelative(-0.001f, scrollbarHeightPerItem * (zaapIndex + 0.5f)))
            forceScroll(gameInfo, scrollPoint)
            listOf(zaap)
        }
    }

    private fun forceScroll(gameInfo: GameInfo, clickLocation: PointRelative) {
        MouseUtil.doubleLeftClick(gameInfo, clickLocation, 100)
    }

    private fun zaapToDestination(gameInfo: GameInfo, zaapDestinations: List<DofusMap>) {
        val sortingMode = TransportSortingUtil.getZaapSortingMode()
        val regionButtonLocation = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "lbl_tabAreaName")
            .getCenter()
        if (sortingMode.sortCriteria != "areaName") {
            MouseUtil.leftClick(gameInfo, regionButtonLocation, 500)
        }
        if (sortingMode.descendingSort) {
            MouseUtil.leftClick(gameInfo, regionButtonLocation, 500)
        }
        WaitUtil.sleep(1000)
        val zaapDestinationIndex = zaapDestinations.indexOf(zaap)
        val firstElementBounds = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "btn_zaapCoord")
        val firstElementLocation = firstElementBounds.getCenter()
        val totalElementsHeight = UiUtil.getContainerBounds(DofusUIElement.ZAAP_SELECTION, "gd_zaap").height
        val dy = totalElementsHeight / 10f
        val zaapLocation = firstElementLocation.getSum(PointRelative(0f, dy * zaapDestinationIndex))
        gameInfo.eventStore.clear()
        WaitUtil.sleep(300)
        MouseUtil.tripleLeftClick(gameInfo, zaapLocation)
        MoveUtil.waitForMapChangeFinished(gameInfo)
    }

    private fun getOrderedZaapDestinations(zaapDestinations: List<DofusMap>): List<DofusMap> {
        val favoriteZaaps = TransportSortingUtil.getFavoriteZaapMapIds().map { it.toInt() }
        val favoriteIndexFunc: (DofusMap) -> String = { if (favoriteZaaps.contains(it.id.toInt())) "A" else "B" }
        return zaapDestinations.sortedBy { StringUtil.removeAccents(favoriteIndexFunc(it) + it.subArea.area.name + it.subArea.name) }
    }

    override fun onStarted(): String {
        val coordinates = zaap.getCoordinates()
        return "Zapping toward [${coordinates.x}, ${coordinates.y}] ..."
    }
}