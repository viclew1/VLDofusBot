package fr.lewon.dofus.bot.model.maps

import fr.lewon.dofus.bot.game.move.Direction

data class DofusMapWithDirection(
    var dofusMap: DofusMap = DofusMap(),
    var direction: Direction = Direction.values()[0]
)