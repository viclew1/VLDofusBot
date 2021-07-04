package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.info.GameInfo
import fr.lewon.dofus.bot.gui.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.util.filemanagers.DTBConfigManager
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.event.KeyEvent

class ReachHavenBagTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (!GameInfo.inHavenBag) {
            MouseUtil.leftClick(DTBConfigManager.config.mouseRestPos)
            KeyboardUtil.sendKey(KeyEvent.VK_H)
            WaitUtil.waitUntil({ GameInfo.inHavenBag })
        }
        return true
    }

    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}