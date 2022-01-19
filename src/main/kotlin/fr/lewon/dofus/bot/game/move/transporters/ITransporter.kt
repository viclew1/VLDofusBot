package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.model.maps.DofusMap

interface ITransporter {

    fun getMap(): DofusMap
    fun getTransporterMap(): DofusMap
    fun getNpcId(): Int
    fun getOptionIndex(): Int

}