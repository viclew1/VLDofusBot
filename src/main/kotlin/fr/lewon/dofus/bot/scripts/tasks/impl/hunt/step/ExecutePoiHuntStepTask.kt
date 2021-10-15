package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

class ExecutePoiHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToPOI) : DofusBotTask<DofusMap>() {

    override fun execute(logItem: LogItem): DofusMap {
        val hint = HintManager.getHint(GameInfo.currentMap, huntStep.direction, huntStep.label)
        val dToHint = hint?.d ?: 1
        val moveTask = MoveUtil.buildMoveTask(huntStep.direction)
        val hunt = TreasureHuntUtil.getTreasureHunt()

        for (i in 0 until dToHint) {
            moveTask.run(logItem)
        }

        while (hunt.startMap == GameInfo.currentMap || hunt.huntFlags.firstOrNull { it.map == GameInfo.currentMap } != null) {
            moveTask.run(logItem)
        }
        TreasureHuntUtil.tickFlag(hunt.huntFlags.size)

        if (hint == null) {
            TreasureHuntUtil.clickSearch()
            WaitUtil.sleep(300)
            TreasureHuntUtil.refreshHuntIfNeeded(logItem)
        }

        return GameInfo.currentMap
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for [${huntStep.label}]"
    }

}