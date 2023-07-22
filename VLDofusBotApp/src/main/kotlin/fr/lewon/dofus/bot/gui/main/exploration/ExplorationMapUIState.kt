package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.foundation.ScrollState
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MapDrawCell
import fr.lewon.dofus.bot.model.characters.DofusCharacter

data class ExplorationMapUIState(
    val hoveredMapDrawCell: MapDrawCell? = null,
    val selectedSubAreaIds: List<Double> = emptyList(),
    val selectedSubAreaIndex: Int = -1,
    val areaExploredByCharacter: Map<DofusCharacter, DofusSubArea> = emptyMap(),
    val horizontalScrollState: ScrollState = ScrollState(0),
    val verticalScrollState: ScrollState = ScrollState(0),
)