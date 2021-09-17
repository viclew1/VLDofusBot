package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinates

interface ITravelElement {
    fun getCoordinates(): DofusCoordinates
    fun isAltWorld(): Boolean
}