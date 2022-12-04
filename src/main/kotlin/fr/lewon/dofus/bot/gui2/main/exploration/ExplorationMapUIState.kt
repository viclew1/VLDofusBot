package fr.lewon.dofus.bot.gui2.main.exploration

import androidx.compose.ui.geometry.Offset
import fr.lewon.dofus.bot.gui2.main.exploration.map.helper.MapDrawCell

data class ExplorationMapUIState(
    val hoveredMapDrawCell: MapDrawCell? = null,
    val selectedSubAreaId: Double? = null,
    val scale: Float = ExplorationUIUtil.MIN_ZOOM,
    val offset: Offset = Offset.Zero
)