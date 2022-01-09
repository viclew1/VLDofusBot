package fr.lewon.dofus.bot.model.maps

import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction


data class DofusMapWithDirection(
    var dofusMap: DofusMap,
    var direction: Direction
)