package fr.lewon.dofus.bot.gui.main.exploration

import androidx.compose.ui.geometry.Offset
import fr.lewon.dofus.bot.core.model.maps.DofusSubArea
import fr.lewon.dofus.bot.gui.main.exploration.map.helper.MapDrawCell
import fr.lewon.dofus.bot.model.characters.DofusCharacter

data class ExplorationMapUIState(
    val hoveredMapDrawCell: MapDrawCell? = null,
    val selectedMapDrawCell: MapDrawCell? = null,
    val areaExploredByCharacter: Map<DofusCharacter, DofusSubArea> = emptyMap(),
    val scale: Float = ExplorationUIUtil.MIN_ZOOM,
    val offset: Offset = Offset.Zero
)