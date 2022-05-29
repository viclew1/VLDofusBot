package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.d2p.maps.element.GraphicalElement
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.model.hunt.DofusPointOfInterest
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.model.hint.GfxIdsByPoiLabel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object HintManager {

    private lateinit var gfxIdsByPoiLabel: GfxIdsByPoiLabel
    private lateinit var gfxIdsByPoiLabelFile: File

    fun initManager() {
        gfxIdsByPoiLabelFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/hint_gfx_ids_by_label")
        if (gfxIdsByPoiLabelFile.exists()) {
            gfxIdsByPoiLabel = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(gfxIdsByPoiLabelFile)
        } else {
            gfxIdsByPoiLabel = GfxIdsByPoiLabel()
            saveHintStoreContent()
        }
    }

    private fun saveHintStoreContent() {
        with(OutputStreamWriter(FileOutputStream(gfxIdsByPoiLabelFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(gfxIdsByPoiLabel))
            close()
        }
    }

    fun isPointOfInterestOnMap(map: DofusMap, pointOfInterest: DofusPointOfInterest): Boolean {
        val gfxIds = gfxIdsByPoiLabel[pointOfInterest.label] ?: error("Unknown POI element")
        return D2PMapsAdapter.getCompleteCellDataByCellId(map.id)
            .flatMap { it.value.graphicalElements }
            .filter { isValidGraphicalElement(it) }
            .map { D2PElementsAdapter.getElement(it.elementId) }
            .filterIsInstance<NormalGraphicalElementData>()
            .map { it.gfxId }
            .intersect(gfxIds)
            .isNotEmpty()
    }

    private fun isValidGraphicalElement(ge: GraphicalElement): Boolean {
        val cellId = ge.cell.cellId
        val mapCellsCount = DofusBoard.MAP_CELLS_COUNT
        val mapWidth = DofusBoard.MAP_WIDTH
        val cellHalfHeight = D2PMapsAdapter.CELL_HALF_HEIGHT
        val cellHalfWidth = D2PMapsAdapter.CELL_HALF_WIDTH
        val topOk = cellId >= mapWidth * 2 || ge.pixelOffset.y >= 0
        val bottomOk = cellId <= mapCellsCount - mapWidth * 2 || ge.pixelOffset.y <= cellHalfHeight
        val divideLeftover = cellId % mapWidth
        val leftOk = divideLeftover > 0 || ge.pixelOffset.x >= -cellHalfWidth
        val rightOk = divideLeftover < mapWidth - 1 || ge.pixelOffset.x <= cellHalfWidth * 3f
        return topOk && bottomOk && leftOk && rightOk
    }

    fun addHintGfxMatch(pointOfInterestLabel: String, gfxId: Int) {
        gfxIdsByPoiLabel.computeIfAbsent(pointOfInterestLabel) { ArrayList() }
            .takeIf { !it.contains(gfxId) }
            ?.add(gfxId)
        saveHintStoreContent()
    }

    fun removeHintGfxMatch(pointOfInterestLabel: String) {
        gfxIdsByPoiLabel.remove(pointOfInterestLabel)
        saveHintStoreContent()
    }
}