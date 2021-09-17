package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import java.awt.event.KeyEvent

class LeaveHavenBagTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (GameInfo.inHavenBag) {
            MouseUtil.leftClick(DTBConfigManager.config.mouseRestPos)
            KeyboardUtil.sendKey(KeyEvent.VK_H, 0)
            EventStore.waitForEvent(MapComplementaryInformationsDataMessage::class.java)
            EventStore.waitForEvent(BasicNoOperationMessage::class.java)
        }
        return true
    }

    override fun onStarted(): String {
        return "Leaving haven bag ..."
    }
}