package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.complex.MultimapMoveTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.io.WaitUtil

class ExecutePoiHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToPOI) : BooleanDofusBotTask() {

    override fun execute(logItem: LogItem): Boolean {
        val hint = HintManager.getHint(GameInfo.currentMap, huntStep.direction, huntStep.label)
        val dToHint = hint?.d ?: 1
        val hunt = TreasureHuntUtil.getTreasureHunt()

        if (!MultimapMoveTask(huntStep.direction, dToHint).run(logItem)) {
            return false
        }
        while (hunt.startMap == GameInfo.currentMap || hunt.huntFlags.firstOrNull { it.map == GameInfo.currentMap } != null) {
            VldbLogger.info("Invalid map, a flag has already been put here.")
            if (!MoveUtil.buildMoveTask(huntStep.direction).run(logItem)) {
                return false
            }
        }
        TreasureHuntUtil.tickFlag(hunt.huntFlags.size)

        if (hint == null) {
            TreasureHuntUtil.clickSearch()
            WaitUtil.sleep(300)
        }

        return true
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for [${huntStep.label}]"
    }

}