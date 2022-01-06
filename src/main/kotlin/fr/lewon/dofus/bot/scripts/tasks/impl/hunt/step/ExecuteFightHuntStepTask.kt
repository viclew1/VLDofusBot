package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecuteFightHuntStepTask : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        return TreasureHuntUtil.fight(logItem, gameInfo)
    }

    override fun onStarted(): String {
        return "Hunt step : fight ..."
    }

}