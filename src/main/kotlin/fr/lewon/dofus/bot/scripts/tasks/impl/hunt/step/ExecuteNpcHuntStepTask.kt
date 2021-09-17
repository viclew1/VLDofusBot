package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        val moveTask = huntStep.direction.buildMoveTask()
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