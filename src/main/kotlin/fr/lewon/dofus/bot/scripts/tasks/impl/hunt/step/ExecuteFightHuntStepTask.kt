package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil

class ExecuteFightHuntStepTask : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        return TreasureHuntUtil.fight(logItem)
    }

    override fun onStarted(): String {
        return "Executing hunt fight step ..."
    }

}