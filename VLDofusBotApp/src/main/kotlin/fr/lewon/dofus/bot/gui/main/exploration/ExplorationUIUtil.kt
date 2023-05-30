package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.HiddenWorldMapHelper
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MainWorldMapHelper
import fr.lewon.dofus.bot.util.filemanagers.impl.ExplorationRecordManager

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
    val colorByMapId = mutableStateOf(HashMap<Double, Color>())

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

    @Synchronized
    fun refreshColors() {
        val newColorByMap = HashMap<Double, Color>()
        val now = System.currentTimeMillis()
        val maxExplorationAge = 2 * 3600 * 1000
        val oldestExplorationTime = now - maxExplorationAge
        for ((mapId, time) in ExplorationRecordManager.getExploredTimeByMapId()) {
            val lastExploreTime = maxOf(oldestExplorationTime, time)
            val red = 255 * (now - lastExploreTime) / maxExplorationAge
            val blue = 255 - red
            newColorByMap[mapId] = Color(red.toInt(), 0, blue.toInt())
        }
        colorByMapId.value = newColorByMap
    }

}