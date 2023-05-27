package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.d2o.managers.hunt.PointOfInterestManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.impl.TreasureHintManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExecutePoiHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToPOI) : BooleanDofusBotTask() {

    private val pointOfInterest = PointOfInterestManager.getPointOfInterest(huntStep.poiLabelId)
        ?: error("No POI for id : ${huntStep.poiLabelId}")

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val stopFunc: (DofusMap, Int) -> Boolean = { map, _ ->
            TreasureHintManager.isPointOfInterestOnMap(map, pointOfInterest)
        }
        val path = MoveUtil.buildDirectionalPath(gameInfo, Direction.fromInt(huntStep.direction), stopFunc, 10)
            ?: error("No map with hint found")
        return MoveTask(path).run(logItem, gameInfo)
    }

    override fun onStarted(): String {
        return "Hunt step : [${Direction.fromInt(huntStep.direction)}] - ${pointOfInterest.label} ..."
    }

}