package fr.lewon.dofus.bot.gui2.main.exploration.map.helper

import androidx.compose.ui.geometry.Offset

data class MapDrawCell(
    val mapId: Double,
    val subAreaId: Double,
    val x: Int,
    val y: Int,
    val topLeft: Offset,
    val topRight: Offset,
    val bottomLeft: Offset,
    val bottomRight: Offset,
    val leftWall: Boolean,
    val bottomWall: Boolean,
    val rightWall: Boolean,
    val topWall: Boolean
)