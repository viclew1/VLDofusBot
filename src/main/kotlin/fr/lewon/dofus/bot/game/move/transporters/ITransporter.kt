package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinate
import fr.lewon.dofus.bot.util.geometry.PointRelative

interface ITransporter {

    fun getTargetCoordinates(): DofusCoordinate

    fun getClosestZaap(): Zaap

    fun getTransporterCoordinates(): DofusCoordinate

    fun getNpcPointRelative(): PointRelative

    fun getOptionPointRelative(): PointRelative

}