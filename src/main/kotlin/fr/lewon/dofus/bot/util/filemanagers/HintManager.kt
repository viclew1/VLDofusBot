package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.model.hunt.DofusPointOfInterest
import fr.lewon.dofus.bot.core.model.maps.DofusMap
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
            .filter { it.value.cellData.mapChangeData == 0 }
            .flatMap { it.value.graphicalElements }
            .map { D2PElementsAdapter.getElement(it.elementId) }
            .filterIsInstance<NormalGraphicalElementData>()
            .map { it.gfxId }
            .intersect(gfxIds)
            .isNotEmpty()
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