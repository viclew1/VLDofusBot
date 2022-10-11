package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.ui.managers.DofusUIElement
import fr.lewon.dofus.bot.scripts.tasks.BooleanDofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.storage.StorageInventoryContentMessage
import fr.lewon.dofus.bot.sniffer.model.types.element.InteractiveElement
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import fr.lewon.dofus.bot.util.ui.UiUtil

class AccessHavenBagChestTask : BooleanDofusBotTask() {

    companion object {
        private const val CHEST_GFX_ID = 12367
        private const val SKILL_ID = 184
    }

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (!ReachHavenBagTask().run(logItem, gameInfo)) {
            error("Couldn't reach haven bag")
        }
        WaitUtil.sleep(1000)
        gameInfo.eventStore.clear()
        val interactiveElement = findChestElement(gameInfo)
            ?: error("Couldn't find chest interactive element")
        InteractiveUtil.useInteractive(gameInfo, interactiveElement.elementId, SKILL_ID)
        WaitUtil.waitUntilMessageArrives(gameInfo, StorageInventoryContentMessage::class.java)
        if (!WaitUtil.waitUntil({ UiUtil.isUiElementWindowOpened(gameInfo, DofusUIElement.STORAGE) })) {
            error("Couldn't open chest storage")
        }
        return true
    }

    private fun findChestElement(gameInfo: GameInfo): InteractiveElement? {
        for (interactiveElement in gameInfo.interactiveElements) {
            val destCellCompleteData = gameInfo.completeCellDataByCellId.values
                .firstOrNull { it.graphicalElements.map { ge -> ge.identifier }.contains(interactiveElement.elementId) }
                ?: error("No cell data found for element : ${interactiveElement.elementId}")

            val graphicalElement = destCellCompleteData.graphicalElements
                .firstOrNull { it.identifier == interactiveElement.elementId }
                ?: error("No graphical element found for element : ${interactiveElement.elementId}")

            val elementData = D2PElementsAdapter.getElement(graphicalElement.elementId)
            if (elementData is NormalGraphicalElementData && elementData.gfxId == CHEST_GFX_ID) {
                return interactiveElement
            }
        }
        return null
    }

    override fun onStarted(): String {
        return "Accessing haven bag chest ... "
    }
}