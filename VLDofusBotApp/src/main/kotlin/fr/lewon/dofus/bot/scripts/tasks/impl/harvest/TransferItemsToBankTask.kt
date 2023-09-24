package fr.lewon.dofus.bot.scripts.tasks.impl.harvest

import fr.lewon.dofus.bot.core.d2o.managers.map.MapManager
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.scripts.tasks.impl.npc.NpcSpeakTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachMapTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.inventory.storage.StorageObjectsUpdateMessage
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class TransferItemsToBankTask : BooleanDofusBotTask() {

    private val bankMap = MapManager.getDofusMap(91753985.0)
    private val bankerNpcId = 100

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val initialPosition = gameInfo.currentMap
        if (!ReachMapTask(destMaps = listOf(bankMap), harvestEnabled = false).run(logItem, gameInfo)) {
            error("Failed to reach bank")
        }
        if (!NpcSpeakTask(bankerNpcId, listOf(-1)).run(logItem, gameInfo)) {
            error("Couldn't talk to banker")
        }
        if (!WaitUtil.waitUntil { UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.STORAGE) }) {
            error("Couldn't open bank UI")
        }
        val resourcesButtonBounds = UiUtil.getContainerBounds(DofusUIElement.STORAGE, "btnRessources")
        MouseUtil.leftClick(gameInfo, resourcesButtonBounds.getCenterRight())
        WaitUtil.sleep(1000)
        gameInfo.eventStore.clear()
        val transferButtonBounds = UiUtil.getContainerBounds(DofusUIElement.STORAGE, "btn_moveAllToLeft")
        val transferOption = GlobalConfigManager.readConfig().transferItemsToBankBehaviour.optionIndex
        val buttonCenter = transferButtonBounds.getCenter()
        if (!InteractiveUtil.clickButtonWithOptions(gameInfo, buttonCenter, transferOption, false)) {
            error("Couldn't transfer all resources")
        }
        if (!WaitUtil.waitUntil { gameInfo.eventStore.getLastEvent(StorageObjectsUpdateMessage::class.java) != null }) {
            error("Items transfer failed")
        }
        WaitUtil.sleep(1000)
        val closeButtonBounds = UiUtil.getContainerBounds(DofusUIElement.STORAGE, "btn_close")
        MouseUtil.leftClick(gameInfo, closeButtonBounds.getCenter())
        if (!WaitUtil.waitUntil { !UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.STORAGE) }) {
            error("Couldn't close bank UI")
        }
        if (gameInfo.shouldReturnToBank()) {
            error("Couldn't transfer enough items, character is still full")
        }
        if (!ReachMapTask(listOf(initialPosition)).run(logItem, gameInfo)) {
            error("Couldn't return to original position")
        }
        return true
    }

    override fun onStarted(): String = "Transferring items to bank ..."
}