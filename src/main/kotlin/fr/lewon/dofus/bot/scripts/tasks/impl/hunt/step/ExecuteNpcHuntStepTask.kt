package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTask
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.treasureHunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        for (i in 0 until 10) {
            val path = MoveUtil.buildDirectionalPath(gameInfo, Direction.fromInt(huntStep.direction), 1)
                ?: error("Couldn't find NPC")
            if (!MoveTask(path).run(logItem, gameInfo)) {
                return false
            }
            if (TreasureHuntUtil.getTreasureHunt(gameInfo).flags.firstOrNull { it.mapId == gameInfo.currentMap.id } == null && gameInfo.drhellerOnMap) {
                return true
            }
        }
        return false
    }

    override fun onStarted(): String {
        return "Hunt step : [${Direction.fromInt(huntStep.direction)}] - Drheller ..."
    }

}