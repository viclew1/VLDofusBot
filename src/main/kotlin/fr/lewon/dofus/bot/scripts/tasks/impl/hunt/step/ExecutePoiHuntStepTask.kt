package fr.lewon.dofus.bot.scripts.tasks.impl.hunt.step

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.scripts.CancellationToken
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MultimapMoveTask
import fr.lewon.dofus.bot.sniffer.model.types.hunt.TreasureHuntStepFollowDirectionToPOI
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.game.TreasureHuntUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class ExecutePoiHuntStepTask(private val huntStep: TreasureHuntStepFollowDirectionToPOI) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo, cancellationToken: CancellationToken): Boolean {
        val hint = HintManager.getHint(gameInfo.currentMap, huntStep.direction, huntStep.label)
        val dToHint = hint?.d ?: 1
        val hunt = TreasureHuntUtil.getTreasureHunt(gameInfo)

        if (!MultimapMoveTask(huntStep.direction, dToHint).run(logItem, gameInfo, cancellationToken)) {
            return false
        }
        while (hunt.startMap == gameInfo.currentMap || hunt.huntFlags.firstOrNull { it.map == gameInfo.currentMap } != null) {
            VldbLogger.info("Invalid map, a flag has already been put here.", logItem)
            if (!MoveUtil.buildMoveTask(huntStep.direction).run(logItem, gameInfo, cancellationToken)) {
                return false
            }
        }
        TreasureHuntUtil.tickFlag(gameInfo, hunt.huntFlags.size, cancellationToken)

        if (hint == null) {
            TreasureHuntUtil.clickSearch(gameInfo, cancellationToken)
        }

        return true
    }

    override fun onStarted(): String {
        return "Executing hunt step : looking for [${huntStep.label}]"
    }

}