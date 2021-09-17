package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.model.maps.DofusMap
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil

class ExecuteFightHuntStepTask() : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        TreasureHuntUtil.fight(logItem)
        return GameInfo.currentMap
    }

    override fun onStarted(): String {
        return "Executing hunt fight step ..."
    }

}