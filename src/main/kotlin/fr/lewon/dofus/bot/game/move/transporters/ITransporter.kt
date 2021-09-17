package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.util.geometry.PointRelative

interface ITransporter : ITravelElement {

    fun getTransporterCoordinates(): DofusCoordinates
    fun getClosestZaap(): Zaap
    fun getNpcPointRelative(): PointRelative
    fun getOptionPointRelative(): PointRelative

}