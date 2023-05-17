package fr.lewon.dofus.bot.scripts.tasks.impl.inventory

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

class UseItemInInventoryTask(private val searchName: String) : BooleanDofusBotTask() {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        MouseUtil.leftClick(gameInfo, MousePositionsUtil.getRestPosition(gameInfo))
        KeyboardUtil.sendKey(gameInfo, 'i')
        val inventoryUiPosition = DofusUIElement.INVENTORY.getPosition()
        return true
    }

    override fun onStarted(): String {
        return "Using item in inventory : $searchName ..."
    }
}