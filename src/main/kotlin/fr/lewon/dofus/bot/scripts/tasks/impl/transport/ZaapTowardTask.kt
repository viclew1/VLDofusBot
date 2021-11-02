package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.manager.DofusUIPositionsManager
import fr.lewon.dofus.bot.core.manager.ui.UIBounds
import fr.lewon.dofus.bot.core.manager.ui.UIPoint
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.move.transporters.Zaap
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.maps.MapInformation
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleRelative
import fr.lewon.dofus.bot.util.io.*

class ZaapTowardTask(private val zaap: Zaap) : DofusBotTask<DofusMap>() {

    companion object {
        private val REF_TOP_LEFT_LOCATION = PointRelative(0.21373057f, 0.10194175f)
        private val REF_TELEPORT_LOCATION = PointRelative(0.49473688f, 0.73519737f)
        private val REF_FIRST_ITEM_LOCATION = PointRelative(0.4355263f, 0.29276314f)
        private val REF_SEARCH_LOCATION = PointRelative(0.6144737f, 0.20559211f)

        private val REF_DELTA_TELEPORT_LOCATION = REF_TELEPORT_LOCATION.getDifference(REF_TOP_LEFT_LOCATION)
        private val REF_DELTA_FIRST_ITEM_LOCATION = REF_FIRST_ITEM_LOCATION.getDifference(REF_TOP_LEFT_LOCATION)
        private val REF_DELTA_SEARCH_LOCATION = REF_SEARCH_LOCATION.getDifference(REF_TOP_LEFT_LOCATION)

        private val REF_CLOSE_ZAAP_SELECTION_BUTTON_BOUNDS = RectangleRelative.build(
            PointRelative(0.75259066f, 0.100323625f),
            PointRelative(0.7797927f, 0.13268608f)
        )
    }

    private lateinit var firstItemLocation: PointRelative
    private lateinit var searchLocation: PointRelative
    private lateinit var teleportLocation: PointRelative
    private lateinit var closeZaapSelectionButtonBounds: RectangleRelative

    override fun execute(logItem: LogItem): DofusMap {
        ReachHavenBagTask().run(logItem)
        WaitUtil.sleep(1500)
        MouseUtil.leftClick(ConfigManager.config.havenBagZaapPos, false, 0)
        val zaapUiCenterPoint = DofusUIPositionsManager.getZaapSelectionUiPosition() ?: UIPoint()
        val zaapUiPoint = UIPoint(
            UIBounds.CENTER.x - 750 / 2 + 5 + zaapUiCenterPoint.x,
            UIBounds.CENTER.y - 710 / 2 - 60 + 5 + zaapUiCenterPoint.y
        )
        val zaapUiPointRelative = ConverterUtil.toPointRelative(zaapUiPoint)
        updateLocations(zaapUiPointRelative)
        if (!WaitUtil.waitUntil({ isZaapSelectionOpened() })) {
            error("Couldn't find zaap selection frame")
        }
        val currentChar = CharacterManager.getCurrentCharacter() ?: error("No character selected")
        val zaapDestination = currentChar.zaapDestinations
            .firstOrNull { it.getMap().getCoordinates() == zaap.getCoordinates() }
            ?: error("Could not find zaap destination [${zaap.name}]. Did you explore it with this character ?")

        MouseUtil.leftClick(searchLocation, false, 500)
        KeyboardUtil.writeKeyboard(getUniqueIdentifier(zaapDestination, currentChar.zaapDestinations), 500)
        MouseUtil.leftClick(firstItemLocation, false, 500)
        MouseUtil.leftClick(teleportLocation, false, 0)
        return WaitUtil.waitForEvents(
            MapComplementaryInformationsDataMessage::class.java,
            BasicNoOperationMessage::class.java
        ).map
    }

    private fun isZaapSelectionOpened(): Boolean {
        val minColorCross = AppColors.UI_BANNER_BLACK_COLOR_MIN
        val maxColorCross = AppColors.UI_BANNER_BLACK_COLOR_MAX
        val minColorBg = AppColors.UI_BANNER_GREY_COLOR_MIN
        val maxColorBg = AppColors.UI_BANNER_GREY_COLOR_MAX
        return ScreenUtil.colorCount(closeZaapSelectionButtonBounds, minColorCross, maxColorCross) > 0
            && ScreenUtil.colorCount(closeZaapSelectionButtonBounds, minColorBg, maxColorBg) > 0
    }

    private fun getUniqueIdentifier(dest: MapInformation, destinations: ArrayList<MapInformation>): String {
        val destMap = dest.getMap()
        val destSubAreaLabel = destMap.getSubAreaLabel()
        val destinationsMatchingSubAreaLabel = destinations.filter { it.getMap().getSubAreaLabel() == destSubAreaLabel }
        if (destinationsMatchingSubAreaLabel.size == 1) return destSubAreaLabel

        val destAreaLabel = destMap.getAreaLabel()
        val destinationsMatchingAreaLabel = destinations.filter { it.getMap().getAreaLabel() == destAreaLabel }
        if (destinationsMatchingAreaLabel.size == 1) return destAreaLabel

        val coordinates = destMap.getCoordinates()
        error("No unique identifier for destination [${coordinates.x}, ${coordinates.y}]")
    }

    private fun updateLocations(zaapUiPointRelative: PointRelative) {
        firstItemLocation = zaapUiPointRelative.getSum(REF_DELTA_FIRST_ITEM_LOCATION)
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