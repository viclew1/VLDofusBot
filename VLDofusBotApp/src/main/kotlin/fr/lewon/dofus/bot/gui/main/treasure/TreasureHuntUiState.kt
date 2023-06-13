package fr.lewon.dofus.bot.gui.main.treasure

import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.model.characters.DofusCharacter

data class TreasureHuntUiState(
    val hintsLoading: Boolean = false,
    val deleteMode: Boolean = false,
    val hintFilter: String = "",
    val hintsGfxByName: Map<String, List<Int>> = emptyMap(),
    val selectedCharacterName: String? = null,
    val selectedGfx: Int? = null,
    val hintsOnMap: List<Int> = emptyList(),
    val loadedCharacter: DofusCharacter? = null,
    val loadedMap: DofusMap? = null,
)