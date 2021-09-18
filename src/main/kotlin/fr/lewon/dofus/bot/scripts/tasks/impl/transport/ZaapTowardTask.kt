package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.move.transporters.Zaap
import fr.lewon.dofus.bot.model.characters.MapInformation
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.filemanagers.DTBCharacterManager
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.geometry.RectangleAbsolute
import fr.lewon.dofus.bot.util.imagetreatment.MatManager
import fr.lewon.dofus.bot.util.imagetreatment.OpenCvUtil
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem

class ZaapTowardTask(private val zaap: Zaap) : DofusBotTask<DofusMap>() {

    companion object {
        private val REF_TOP_RIGHT_LOCATION = PointRelative(0.7802632f, 0.098684214f)

        private val REF_TELEPORT_LOCATION = PointRelative(0.49473688f, 0.73519737f)
        private val REF_FIRST_ITEM_LOCATION = PointRelative(0.4355263f, 0.29276314f)
        private val REF_SEARCH_LOCATION = PointRelative(0.6144737f, 0.20559211f)

        private val REF_DELTA_TELEPORT_LOCATION = REF_TELEPORT_LOCATION.getDifference(REF_TOP_RIGHT_LOCATION)
        private val REF_DELTA_FIRST_ITEM_LOCATION = REF_FIRST_ITEM_LOCATION.getDifference(REF_TOP_RIGHT_LOCATION)
        private val REF_DELTA_SEARCH_LOCATION = REF_SEARCH_LOCATION.getDifference(REF_TOP_RIGHT_LOCATION)
    }

    private lateinit var firstItemLocation: PointRelative
    private lateinit var searchLocation: PointRelative
    private lateinit var teleportLocation: PointRelative

    override fun execute(logItem: LogItem): DofusMap {
        ReachHavenBagTask().run(logItem)
        WaitUtil.sleep(1500)
        MouseUtil.leftClick(DTBConfigManager.config.havenBagZaapPos, false, 0)
        var topBounds: RectangleAbsolute? = null
        WaitUtil.waitUntil({
            OpenCvUtil.getPatternBounds(MatManager.TOP_ZAAP_MAT.buildMat(), 0.8).also { topBounds = it } != null
        })
        val topRightLocation = topBounds?.let { PointAbsolute(it.x + it.width, it.y) }
            ?: error("Couldn't find zaap window")
        updateLocations(ConverterUtil.toPointRelative(topRightLocation))
        val currentChar = DTBCharacterManager.getCurrentCharacter() ?: error("No character selected")
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

    private fun updateLocations(topRightLocation: PointRelative) {
        firstItemLocation = topRightLocation.getSum(REF_DELTA_FIRST_ITEM_LOCATION)
        teleportLocation = topRightLocation.getSum(REF_DELTA_TELEPORT_LOCATION)
        searchLocation = topRightLocation.getSum(REF_DELTA_SEARCH_LOCATION)
    }

    override fun onStarted(): String {
        val coordinates = zaap.getCoordinates()
        return "Zapping toward [${coordinates.x}, ${coordinates.y}] ..."
    }
}