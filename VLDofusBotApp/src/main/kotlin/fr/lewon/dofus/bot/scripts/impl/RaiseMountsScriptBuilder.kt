package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.model.characters.parameters.ParameterValues
import fr.lewon.dofus.bot.scripts.DofusBotScriptBuilder
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.mount.GameDataPaddockObjectRemoveMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object RaiseMountsScriptBuilder : DofusBotScriptBuilder("Raise mounts") {

    override fun getParameters(): List<DofusBotParameter<*>> {
        return emptyList()
    }

    override fun getDefaultStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "abc"
    }

    override fun doExecuteScript(
        logItem: LogItem,
        gameInfo: GameInfo,
        parameterValues: ParameterValues,
        statValues: HashMap<DofusBotScriptStat, String>,
    ) {
        removeAllPaddockItems(gameInfo)
    }

    private fun removeAllPaddockItems(gameInfo: GameInfo) {
        val paddockItems = gameInfo.paddockItemByCell.values.sortedByDescending { it.cellId }
        for (paddockItem in paddockItems) {
            gameInfo.eventStore.clear()
            val clickPos = InteractiveUtil.getCellClickPosition(gameInfo, paddockItem.cellId, false)
            MouseUtil.leftClick(gameInfo, clickPos, 200)
            MouseUtil.leftClick(gameInfo, clickPos.getSum(PointRelative(0.01f, 0.01f)), 200)
            WaitUtil.waitForEvent(gameInfo, GameDataPaddockObjectRemoveMessage::class.java)
        }
    }
}