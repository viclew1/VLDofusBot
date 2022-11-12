package fr.lewon.dofus.bot.gui2.main.exploration

import androidx.compose.ui.geometry.Offset

data class ExplorationUIState(
    val exploredTimeByMap: Map<Double, Long> = HashMap(),
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero
)