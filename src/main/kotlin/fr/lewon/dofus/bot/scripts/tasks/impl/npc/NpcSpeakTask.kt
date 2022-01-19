package fr.lewon.dofus.bot.scripts.tasks.impl.npc

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.LeaveDialogMessage
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.NpcDialogCreationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.interactive.NpcDialogQuestionMessage
import fr.lewon.dofus.bot.sniffer.model.messages.misc.BasicNoOperationMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.GameInfo

class NpcSpeakTask(private val npcId: Int, private vararg val optionIndexes: Int) :
    DofusBotTask<Boolean>() {

    companion object {
        private val FIRST_OPTION_LOCATION = PointRelative(0.36847493f, 0.7442455f)
        private val FIFTH_OPTION_LOCATION = PointRelative(0.36847493f, 0.6508951f)
        private val DELTA_OPTION = (FIRST_OPTION_LOCATION.y - FIFTH_OPTION_LOCATION.y) / 4f
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        val npcLocation = InteractiveUtil.getNpcClickPosition(gameInfo, npcId)
        MouseUtil.leftClick(gameInfo, npcLocation)
        gameInfo.eventStore.clear()
        WaitUtil.waitForEvents(gameInfo, NpcDialogCreationMessage::class.java)
        for (optionIndex in optionIndexes) {
            WaitUtil.waitForEvents(gameInfo, NpcDialogQuestionMessage::class.java, BasicNoOperationMessage::class.java)
            WaitUtil.sleep(300)
            val optionLocation = FIRST_OPTION_LOCATION.getSum(PointRelative(0f, -optionIndex * DELTA_OPTION))
            MouseUtil.leftClick(gameInfo, optionLocation)
            gameInfo.eventStore.clear()
        }
        WaitUtil.waitForEvents(gameInfo, LeaveDialogMessage::class.java, BasicNoOperationMessage::class.java)
        gameInfo.eventStore.clearUntilLast(LeaveDialogMessage::class.java)
        return true
    }

    override fun onStarted(): String {
        return "Interacting with NPC ..."
    }
}