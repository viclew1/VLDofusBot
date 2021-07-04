package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToHint

class ExecuteNpcHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToHint) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        val moveTask = huntStep.direction.buildMoveTask()
        for (i in 0 until 10) {
            moveTask.run(logItem)
            if (GameInfo.phorrorOnMap) return GameInfo.currentMap
        }
        error("Couldn't find a phorror")
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for phorror"
    }

}