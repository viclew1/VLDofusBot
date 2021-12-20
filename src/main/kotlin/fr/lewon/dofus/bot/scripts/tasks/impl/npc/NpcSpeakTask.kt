package fr.lewon.dofus.bot.scripts.tasks.impl.npc

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.LeaveDialogMessage
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.NpcDialogCreationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class NpcSpeakTask(private val npcLocation: PointRelative, private val optionLocation: PointRelative) :
    DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        MouseUtil.leftClick(gameInfo, npcLocation)
        gameInfo.eventStore.clear()
        WaitUtil.waitForEvents(
            gameInfo,
            NpcDialogCreationMessage::class.java,
            BasicNoOperationMessage::class.java,
        )
        WaitUtil.sleep(300)
        MouseUtil.leftClick(gameInfo, optionLocation)
        gameInfo.eventStore.clear()
        WaitUtil.waitForEvents(
            gameInfo,
            LeaveDialogMessage::class.java,
            BasicNoOperationMessage::class.java,
        )
        gameInfo.eventStore.clearUntilLast(LeaveDialogMessage::class.java)
        return true
    }

    override fun onStarted(): String {
        return "Interacting with NPC ..."
    }
}