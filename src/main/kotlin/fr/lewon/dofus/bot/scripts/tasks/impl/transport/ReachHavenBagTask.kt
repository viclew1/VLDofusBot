package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import java.awt.event.KeyEvent

class ReachHavenBagTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (!GameInfo.inHavenBag) {
            while (!tryToReachHavenBag()) {
                val lastMove = MoveHistory.getLastMove() ?: error("Failed to reach haven bag")
                lastMove.direction.getReverseDir().buildMoveTask().run(logItem)
                MoveHistory.getLastMove()
            }
        }
        return true
    }

    private fun tryToReachHavenBag(): Boolean {
        MouseUtil.leftClick(DTBConfigManager.config.mouseRestPos)
        KeyboardUtil.sendKey(KeyEvent.VK_H, 0)
        EventStore.waitForEventWithoutError(MapComplementaryInformationsDataInHavenBagMessage::class.java, 5000)
            ?: return false
        EventStore.waitForEvent(BasicNoOperationMessage::class.java)
        return true
    }

    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}