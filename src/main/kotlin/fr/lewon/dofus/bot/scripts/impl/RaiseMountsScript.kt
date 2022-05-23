package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptStat
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.sniffer.model.messages.breeding.GameDataPaddockObjectRemoveMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class RaiseMountsScript : DofusBotScript("Raise mounts") {

    override fun getParameters(): List<DofusBotParameter> {
        return emptyList()
    }

    override fun getStats(): List<DofusBotScriptStat> {
        return emptyList()
    }

    override fun getDescription(): String {
        return "abc"
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo) {
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