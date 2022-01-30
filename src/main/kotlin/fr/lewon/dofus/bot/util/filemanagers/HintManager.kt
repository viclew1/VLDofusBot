package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.core.d2o.managers.hunt.PointOfInterestManager
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.model.hunt.DofusPointOfInterest
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import fr.lewon.dofus.bot.model.hint.HintSet
import fr.lewon.dofus.bot.model.hint.HintStoreContent
import fr.lewon.dofus.bot.sniffer.model.messages.treasurehunt.TreasureHuntMessage
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.game.MoveUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object HintManager {

    private lateinit var hintStoreContent: HintStoreContent
    private lateinit var hintStoreContentFile: File

    fun initManager() {
        hintStoreContentFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/hint_store_content")
        if (hintStoreContentFile.exists()) {
            hintStoreContent = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(hintStoreContentFile)
        } else {
            val inputStream = VLDofusBot::class.java.getResourceAsStream("/default_hint_store_content.json")
                ?: error("No IS for hint store")
            hintStoreContent = ObjectMapper().readValue(inputStream, HintStoreContent::class.java)
            saveHintStoreContent()
        }
    }

    private fun saveHintStoreContent() {
        with(OutputStreamWriter(FileOutputStream(hintStoreContentFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(hintStoreContent))
            close()
        }
    }

    private fun getHintsOnMap(map: DofusMap): HintSet {
        return hintStoreContent.hintStore.computeIfAbsent(buildMapKey(map)) { HintSet() }
    }

    private fun buildMapKey(map: DofusMap): String {
        return "${map.posX}_${map.posY}_${map.worldMap != 1}"
    }

    private fun getHintId(pointOfInterest: DofusPointOfInterest): Int {
        return hintStoreContent.labelStore.computeIfAbsent(pointOfInterest.label) {
            if (hintStoreContent.labelStore.values.firstOrNull { it == pointOfInterest.id } == null) {
                pointOfInterest.id
            } else {
                (hintStoreContent.labelStore.values.maxOrNull() ?: 0) + 1
            }
        }
    }

    fun isPointOfInterestOnMap(map: DofusMap, pointOfInterest: DofusPointOfInterest): Boolean {
        val hintId = getHintId(pointOfInterest)
        return getHintsOnMap(map).contains(hintId)
    }

    fun updateHints(succeededHunt: TreasureHuntMessage) {
        var currentMap = succeededHunt.startMap
        for (i in 0 until succeededHunt.huntSteps.size) {
            val flag = succeededHunt.huntFlags[i]
            val step = succeededHunt.huntSteps[i]
            if (step is TreasureHuntStepFollowDirectionToPOI) {
                val pointOfInterest = PointOfInterestManager.getPointOfInterest(step.poiLabelId)
                    ?: error("Unknown POI : ${step.poiLabelId}")
                val startVertex = WorldGraphUtil.getVertex(currentMap.id, 1)
                    ?: error("No vertex found")
                MoveUtil.buildDirectionalPath(startVertex, step.direction, { map, _ ->
                    removeHint(map, pointOfInterest)
                    map == flag.map
                }, 10) ?: error("Couldn't redo hunt")
                addHint(flag.map, pointOfInterest)
            }
            currentMap = flag.map
        }
        saveHintStoreContent()
    }

    private fun addHint(map: DofusMap, pointOfInterest: DofusPointOfInterest) {
        if (!isPointOfInterestOnMap(map, pointOfInterest)) {
            getHintsOnMap(map).add(getHintId(pointOfInterest))
        }
    }

    private fun removeHint(map: DofusMap, pointOfInterest: DofusPointOfInterest) {
        if (isPointOfInterestOnMap(map, pointOfInterest)) {
            getHintsOnMap(map).remove(getHintId(pointOfInterest))
        }
    }
}