package fr.lewon.dofus.bot.sniffer.store.handlers

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.sniffer.model.messages.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler

object ChangeMapEventHandler : EventHandler<MapComplementaryInformationsDataMessage> {

    override fun onEventReceived(socketResult: MapComplementaryInformationsDataMessage) {
        GameInfo.currentMap = socketResult.dofusMap
        GameInfo.phorrorOnMap = socketResult.isPhorrorHere()
        GameInfo.inHavenBag = false
        println("[${socketResult.dofusMap.posX},${socketResult.dofusMap.posY}] ; World : ${socketResult.dofusMap.worldMap} ; Phorror here : ${socketResult.isPhorrorHere()}")
    }

}