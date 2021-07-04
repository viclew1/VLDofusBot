package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.move.transporters.Zaap
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.DTBCharacterManager
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.imagetreatment.MatManager
import fr.lewon.dofus.bot.util.imagetreatment.OpenCvUtil
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.ScreenUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.Color
import java.awt.Rectangle
import java.text.Normalizer
import kotlin.math.ceil

class ZaapTowardTask(private val zaap: Zaap) : DofusBotTask<DofusMap>() {

    companion object {
        private const val ZAAPS_REVEALED_BY_SCROLL = 3
        private val REF_TOP_RIGHT_LOCATION = PointRelative(0.76868606f, 0.13765979f)
        private val REF_SORT_AREA_LOCATION = PointRelative(0.49173883f, 0.28810227f)
        private val REF_FAVORITE_LOCATION = PointRelative(0.23367427f, 0.3284169f)
        private val REF_FRAME_CENTER_LOCATION = PointRelative(0.4681353f, 0.49950835f)
        private val REF_TELEPORT_LOCATION = PointRelative(0.47915024f, 0.77187806f)
        private val REF_FIRST_ITEM_LOCATION = PointRelative(0.31077892f, 0.33038348f)
        private val REF_LAST_ITEM_LOCATION = PointRelative(0.31077892f, 0.6794494f)

        private val REF_DELTA_SORT_AREA_LOCATION = PointRelative(
            REF_SORT_AREA_LOCATION.x - REF_TOP_RIGHT_LOCATION.x, REF_SORT_AREA_LOCATION.y - REF_TOP_RIGHT_LOCATION.y
        )
        private val REF_DELTA_FAVORITE_LOCATION = PointRelative(
            REF_FAVORITE_LOCATION.x - REF_TOP_RIGHT_LOCATION.x, REF_FAVORITE_LOCATION.y - REF_TOP_RIGHT_LOCATION.y
        )
        private val REF_DELTA_FRAME_CENTER_LOCATION = PointRelative(
            REF_FRAME_CENTER_LOCATION.x - REF_TOP_RIGHT_LOCATION.x,
            REF_FRAME_CENTER_LOCATION.y - REF_TOP_RIGHT_LOCATION.y
        )
        private val REF_DELTA_TELEPORT_LOCATION = PointRelative(
            REF_TELEPORT_LOCATION.x - REF_TOP_RIGHT_LOCATION.x, REF_TELEPORT_LOCATION.y - REF_TOP_RIGHT_LOCATION.y
        )
        private val REF_DELTA_FIRST_ITEM_LOCATION = PointRelative(
            REF_FIRST_ITEM_LOCATION.x - REF_TOP_RIGHT_LOCATION.x, REF_FIRST_ITEM_LOCATION.y - REF_TOP_RIGHT_LOCATION.y
        )

        private val ITEM_DELTA_Y = (REF_LAST_ITEM_LOCATION.y - REF_FIRST_ITEM_LOCATION.y) / 9f
        private const val ORDER_SQUARE_WIDTH = 0.0133753f

        private val MIN_FAVORITE_COLOR = Color(215, 245, 0)
        private val MAX_FAVORITE_COLOR = Color(230, 255, 5)
    }

    private lateinit var sortAreaLocation: PointRelative
    private lateinit var favoriteLocation: PointRelative
    private lateinit var firstItemLocation: PointRelative
    private lateinit var centerLocation: PointRelative
    private lateinit var teleportLocation: PointRelative

    override fun execute(logItem: LogItem): DofusMap {
        ReachHavenBagTask().run(logItem)
        MouseUtil.leftClick(DTBConfigManager.config.havenBagZaapPos, false, 0)
        var topBounds: Rectangle? = null
        WaitUtil.waitUntil({
            OpenCvUtil.getPatternBounds(MatManager.TOP_ZAAP_MAT.buildMat(), 0.8).also { topBounds = it } != null
        })
        val topRightLocation = topBounds?.let { PointAbsolute(it.x + it.width, it.y) }
            ?: error("Couldn't find zaap window")
        updateLocations(ConverterUtil.toPointRelative(topRightLocation))
        val currentChar = DTBCharacterManager.getCurrentCharacter() ?: error("No character selected")
        val zaapDestinationsCoordinates = currentChar.zaapDestinations
            .sortedBy { Normalizer.normalize(it.getMap().getSubAreaLabel(), Normalizer.Form.NFD) }
            .sortedBy { Normalizer.normalize(it.getMap().getAreaLabel(), Normalizer.Form.NFD) }
            .map { it.getMap().getCoordinate() }
        val zaapIndex = zaapDestinationsCoordinates.indexOf(zaap.coordinate)
        if (zaapIndex == -1) {
            error("Could not find zaap destination [${zaap.name}]. Did you explore it with this character ?")
        }

        sortByArea(logItem)
        removeFavorites(logItem)

        val scrollAmount = getScrollAmount(zaapIndex)
        MouseUtil.scrollDown(centerLocation, scrollAmount)
        clickZaapItem(zaapIndex - scrollAmount * ZAAPS_REVEALED_BY_SCROLL)
        MouseUtil.leftClick(teleportLocation, false, 0)
        return EventStore.waitForEvent(
            MapComplementaryInformationsDataMessage::class.java,
            DTBConfigManager.config.globalTimeout * 1000
        ).dofusMap
    }

    private fun updateLocations(topRightLocation: PointRelative) {
        sortAreaLocation = PointRelative(
            topRightLocation.x + REF_DELTA_SORT_AREA_LOCATION.x,
            topRightLocation.y + REF_DELTA_SORT_AREA_LOCATION.y
        )
        favoriteLocation = PointRelative(
            topRightLocation.x + REF_DELTA_FAVORITE_LOCATION.x,
            topRightLocation.y + REF_DELTA_FAVORITE_LOCATION.y
        )
        firstItemLocation = PointRelative(
            topRightLocation.x + REF_DELTA_FIRST_ITEM_LOCATION.x,
            topRightLocation.y + REF_DELTA_FIRST_ITEM_LOCATION.y
        )
        centerLocation = PointRelative(
            topRightLocation.x + REF_DELTA_FRAME_CENTER_LOCATION.x,
            topRightLocation.y + REF_DELTA_FRAME_CENTER_LOCATION.y
        )
        teleportLocation = PointRelative(
            topRightLocation.x + REF_DELTA_TELEPORT_LOCATION.x,
            topRightLocation.y + REF_DELTA_TELEPORT_LOCATION.y
        )
    }

    private fun sortByArea(logItem: LogItem) {
        val topLeftSortPointAbsolute = ConverterUtil.toPointAbsolute(
            PointRelative(sortAreaLocation.x - ORDER_SQUARE_WIDTH, sortAreaLocation.y - ORDER_SQUARE_WIDTH)
        )
        val bottomRightSortPointAbsolute = ConverterUtil.toPointAbsolute(
            PointRelative(sortAreaLocation.x + ORDER_SQUARE_WIDTH, sortAreaLocation.y + ORDER_SQUARE_WIDTH)
        )
        val sortAreaBounds = Rectangle(
            topLeftSortPointAbsolute.x,
            topLeftSortPointAbsolute.y,
            bottomRightSortPointAbsolute.x - topLeftSortPointAbsolute.x,
            bottomRightSortPointAbsolute.y - topLeftSortPointAbsolute.y
        )
        while (OpenCvUtil.getPatternBounds(sortAreaBounds, MatManager.ORDER_ASC_MAT.buildMat(), 0.8) == null) {
            MouseUtil.leftClick(sortAreaLocation, false, 800)
        }
    }

    private fun removeFavorites(logItem: LogItem) {
        while (ScreenUtil.isBetween(favoriteLocation, MIN_FAVORITE_COLOR, MAX_FAVORITE_COLOR)) {
            MouseUtil.leftClick(favoriteLocation, false, 800)
        }
    }

    private fun clickZaapItem(index: Int) {
        val zaapLoc = PointRelative(firstItemLocation.x, firstItemLocation.y + index * ITEM_DELTA_Y)
        MouseUtil.leftClick(zaapLoc, false, 300)
    }

    private fun getScrollAmount(zaapIndex: Int): Int {
        if (zaapIndex < 10) return 0
        return ceil((zaapIndex - 9).toFloat() / ZAAPS_REVEALED_BY_SCROLL.toFloat()).toInt()
    }

    override fun onStarted(): String {
        return "Zapping toward [${zaap.coordinate.x}, ${zaap.coordinate.y}]"
    }
}