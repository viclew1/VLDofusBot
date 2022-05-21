package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.model.hunt.DofusPointOfInterest
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.model.hint.ElementIdByPoiLabel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object HintManager {

    private lateinit var elementIdByPoiLabel: ElementIdByPoiLabel
    private lateinit var elementIdByPoiLabelFile: File

    fun initManager() {
        elementIdByPoiLabelFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/hint_element_id_by_label")
        if (elementIdByPoiLabelFile.exists()) {
            elementIdByPoiLabel = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(elementIdByPoiLabelFile)
        } else {
            elementIdByPoiLabel = ElementIdByPoiLabel()
            saveHintStoreContent()
        }
    }

    private fun saveHintStoreContent() {
        with(OutputStreamWriter(FileOutputStream(elementIdByPoiLabelFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(elementIdByPoiLabel))
            close()
        }
    }

    fun isPointOfInterestOnMap(map: DofusMap, pointOfInterest: DofusPointOfInterest): Boolean {
        val elementId = elementIdByPoiLabel[pointOfInterest.label] ?: error("Unknown POI element")
        return D2PMapsAdapter.getCompleteCellDataByCellId(map.id)
            .flatMap { it.value.graphicalElements }
            .map { it.elementId }
            .contains(elementId)
    }

    fun addHintElementMatch(pointOfInterestLabel: String, elementId: Int) {
        elementIdByPoiLabel[pointOfInterestLabel] = elementId
        saveHintStoreContent()
    }
}