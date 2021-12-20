package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.util.geometry.PointRelative

interface ITransporter {

    fun getMap(): DofusMap
    fun getTransporterMap(): DofusMap
    fun getNpcPointRelative(): PointRelative
    fun getOptionPointRelative(): PointRelative

}