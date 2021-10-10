package fr.lewon.dofus.bot.scripts.tasks.impl.npc

import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.LeaveDialogMessage
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.NpcDialogCreationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.logs.LogItem

class NpcSpeakTask(private val npcLocation: PointRelative, private val optionLocation: PointRelative) :
    DofusBotTask<Boolean>() {

    override fun execute(logItem: LogItem): Boolean {
        MouseUtil.leftClick(npcLocation, millis = 0)
        WaitUtil.waitForEvents(NpcDialogCreationMessage::class.java, BasicNoOperationMessage::class.java)
        WaitUtil.sleep(300)
        MouseUtil.leftClick(optionLocation, millis = 0)
        WaitUtil.waitForEvents(LeaveDialogMessage::class.java, BasicNoOperationMessage::class.java)
        return true
    }

    override fun onStarted(): String {
        return "Interacting with NPC ..."
    }
}