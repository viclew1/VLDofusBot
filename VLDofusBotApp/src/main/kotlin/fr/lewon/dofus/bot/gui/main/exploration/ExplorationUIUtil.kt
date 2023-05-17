package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.HiddenWorldMapHelper
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MainWorldMapHelper

object ExplorationUIUtil : ComposeUIUtil() {

    const val MIN_ZOOM = 1f
    const val MAX_ZOOM = 4f
    const val CELL_SIZE = 5f

    val minPosX = MapManager.getAllMaps().minOf { it.posX } - 10
    val minPosY = MapManager.getAllMaps().minOf { it.posY } - 10
    val maxPosX = MapManager.getAllMaps().maxOf { it.posX } + 10
    val maxPosY = MapManager.getAllMaps().maxOf { it.posY } + 10
    val centerX = minPosX + (maxPosX - minPosX) / 2
    val centerY = minPosY + (maxPosY - minPosY) / 2
    val totalWidth = (maxPosX - minPosX + 1) * CELL_SIZE
    val totalHeight = (maxPosY - minPosY + 1) * CELL_SIZE

    val mapUIState = mutableStateOf(ExplorationMapUIState())
    val explorerUIState = mutableStateOf(ExplorationExplorerUIState())
    private val worldMapHelpers = listOf(MainWorldMapHelper, HiddenWorldMapHelper)
    val worldMapHelper = mutableStateOf(worldMapHelpers.first())
    val exploredTimeByMap = mutableStateOf<Map<Double, Long>>(HashMap())

    fun setNextWorldMapHelper() {
        val nextIndex = worldMapHelpers.indexOf(worldMapHelper.value) + 1
        val newWorldMapHelper = if (nextIndex >= worldMapHelpers.size) {
            worldMapHelpers.first()
        } else {
            worldMapHelpers[nextIndex]
        }
        mapUIState.value = mapUIState.value.copy(
            hoveredMapDrawCell = null,
            selectedSubAreaId = null
        )
        worldMapHelper.value = newWorldMapHelper
    }

    fun exploreMap(mapId: Double) {
        exploredTimeByMap.value = exploredTimeByMap.value.plus(mapId to System.currentTimeMillis())
    }

    fun addAvailableCharacter(characterName: String) {
        explorerUIState.value = explorerUIState.value.copy(
            availableCharacters = explorerUIState.value.availableCharacters.plus(characterName),
            selectedCharacterName = if (explorerUIState.value.selectedCharacterName.isNullOrEmpty()) {
                characterName
            } else {
                explorerUIState.value.selectedCharacterName
            },
        )
    }

    fun removeAvailableCharacter(characterName: String) {
        val newAvailable = explorerUIState.value.availableCharacters.minus(characterName)
        explorerUIState.value = explorerUIState.value.copy(
            selectedCharacterName = if (characterName == explorerUIState.value.selectedCharacterName) {
                newAvailable.firstOrNull()
            } else explorerUIState.value.selectedCharacterName,
            availableCharacters = newAvailable
        )
    }

}