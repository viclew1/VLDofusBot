package fr.lewon.dofus.bot.game.move

import fr.lewon.dofus.bot.model.maps.DofusMap

data class Move(val direction: Direction, val fromMap: DofusMap, val toMap: DofusMap)