package fr.lewon.dofus.bot.gui2.main.exploration

import fr.lewon.dofus.bot.core.model.maps.DofusMap

data class MapDrawCell(
    val map: DofusMap,
    val leftWall: Boolean,
    val bottomWall: Boolean,
    val rightWall: Boolean,
    val topWall: Boolean
)