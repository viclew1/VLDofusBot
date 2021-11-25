package fr.lewon.dofus.bot.scripts.tasks.impl.moves

import fr.lewon.dofus.bot.core.manager.DofusMapManager
import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates

class TravelToCoordinatesTask(private val dofusCoordinates: DofusCoordinates) :
    TravelTask(DofusMapManager.getDofusMaps(dofusCoordinates.x, dofusCoordinates.y)) {

    override fun onStarted(): String {
        return "Traveling to [${dofusCoordinates.x}; ${dofusCoordinates.y}] ..."
    }

}