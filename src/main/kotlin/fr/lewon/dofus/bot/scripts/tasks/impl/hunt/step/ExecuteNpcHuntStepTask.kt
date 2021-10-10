package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.logs.LogItem

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        val moveTask = MoveUtil.buildMoveTask(huntStep.direction)
        for (i in 0 until 10) {
            moveTask.run(logItem)
            if (TreasureHuntUtil.getTreasureHunt().huntFlags.firstOrNull { it.map == GameInfo.currentMap } == null && GameInfo.phorrorOnMap) {
                TreasureHuntUtil.tickFlag(TreasureHuntUtil.getTreasureHunt().huntFlags.size)
                return GameInfo.currentMap
            }
        }
        error("Couldn't find a phorror")
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for phorror"
    }

}