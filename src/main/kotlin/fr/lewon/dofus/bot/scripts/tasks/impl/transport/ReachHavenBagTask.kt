package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.game.move.MoveHistory
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.move.MapComplementaryInformationsDataInHavenBagMessage
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.game.MoveUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem
import java.awt.event.KeyEvent

class ReachHavenBagTask : DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        if (!GameInfo.inHavenBag) {
            while (!tryToReachHavenBag()) {
                val lastMove = MoveHistory.getLastMove() ?: error("Failed to reach haven bag")
                MoveUtil.buildMoveTask(lastMove.direction.getReverseDir()).run(logItem)
                MoveHistory.getLastMove()
            }
        }
        return true
    }

    private fun tryToReachHavenBag(): Boolean {
        MouseUtil.leftClick(ConfigManager.config.mouseRestPos)
        KeyboardUtil.sendKey(KeyEvent.VK_H, 0)
        WaitUtil.waitForEventWithoutError(MapComplementaryInformationsDataInHavenBagMessage::class.java, 5000)
            ?: return false
        WaitUtil.waitForEvent(BasicNoOperationMessage::class.java)
        return true
    }

    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}