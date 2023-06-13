package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.runtime.mutableStateOf
import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.gui.ComposeUIUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager
import fr.lewon.dofus.bot.util.network.info.GameInfo

object TreasureHuntUiUtil : ComposeUIUtil() {

    private val uiState = mutableStateOf(TreasureHuntUiState())
    val registeredHintsImageCache = TreasureHintImageCache()
    val mapHintsImageCache = TreasureHintImageCache()

    fun getUiStateValue() = uiState.value

    @Synchronized
    fun refreshRegisteredHints() {
        val newHintsGfxByName = TreasureHintManager.getGfxIdsByPoiLabel().mapValues { it.value.sorted() }
        registeredHintsImageCache.trim(newHintsGfxByName.values.flatten())
        uiState.value = uiState.value.copy(
            hintsGfxByName = newHintsGfxByName
        )
    }

    @Synchronized
    fun loadMapHints(gameInfo: GameInfo) {
        val newGfxIds = getAllMapGfxIds(gameInfo)
        mapHintsImageCache.trim(newGfxIds)
        uiState.value = uiState.value.copy(
            loadedCharacter = gameInfo.character,
            loadedMap = gameInfo.currentMap,
            hintsOnMap = newGfxIds,
            selectedGfx = null
        )
    }

    private fun getAllMapGfxIds(gameInfo: GameInfo): List<Int> =
        gameInfo.mapData.completeCellDataByCellId.flatMap { it.value.graphicalElements }
            .asSequence()
            .map { D2PElementsAdapter.getElement(it.elementId) }
            .filterIsInstance<NormalGraphicalElementData>()
            .map { it.gfxId }
            .plus(gameInfo.mapData.foregroundFixtures.map { it.fixtureId })
            .plus(gameInfo.mapData.backgroundFixtures.map { it.fixtureId })
            .distinct()
            .sorted()
            .toList()

    fun updateHintFilter(filter: String) {
        uiState.value = uiState.value.copy(hintFilter = filter)
    }

    fun toggleDeleteMode() {
        val uiStateValue = getUiStateValue()
        uiState.value = uiStateValue.copy(deleteMode = !uiStateValue.deleteMode)
    }

    fun setSelectedCharacterName(characterName: String) {
        uiState.value = uiState.value.copy(selectedCharacterName = characterName)
    }

    fun setSelectedGfx(gfxId: Int?) {
        uiState.value = uiState.value.copy(selectedGfx = gfxId)
    }

}