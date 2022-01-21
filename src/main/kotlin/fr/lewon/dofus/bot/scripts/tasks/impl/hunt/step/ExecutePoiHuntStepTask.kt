package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.d2o.managers.PointOfInterestManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecutePoiHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToPOI) : BooleanDofusBotTask() {

    private val pointOfInterest = PointOfInterestManager.getPointOfInterest(huntStep.poiLabelId)
        ?: error("No POI for id : ${huntStep.poiLabelId}")

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val stopFunc: (DofusMap, Int) -> Boolean = { map, _ ->
            HintManager.isPointOfInterestOnMap(map, pointOfInterest)
        }
        val path = MoveUtil.buildDirectionalPath(gameInfo, huntStep.direction, stopFunc, 10)
            ?: error("No map with hint found")
        return MoveTask(path).run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        return "Hunt step : [${huntStep.direction}] - ${pointOfInterest.label} ..."
    }

}