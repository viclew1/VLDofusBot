package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataMessage
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem
import java.awt.event.KeyEvent

class LeaveHavenBagTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (GameInfo.inHavenBag) {
            MouseUtil.leftClick(DTBConfigManager.config.mouseRestPos)
            KeyboardUtil.sendKey(KeyEvent.VK_H, 0)
            WaitUtil.waitForEvent(MapComplementaryInformationsDataMessage::class.java)
            WaitUtil.waitForEvent(BasicNoOperationMessage::class.java)
        }
        return true
    }

    override fun onStarted(): String {
        return "Leaving haven bag ..."
    }
}