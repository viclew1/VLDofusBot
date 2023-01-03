package fr.lewon.dofus.bot.scripts.tasks.impl.npc

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.scripts.tasks.DofusBotTask
import fr.lewon.dofus.bot.sniffer.model.messages.game.basic.BasicNoOperationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc.NpcDialogCreationMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.npc.NpcDialogQuestionMessage
import fr.lewon.dofus.bot.sniffer.model.messages.game.dialog.LeaveDialogMessage
import fr.lewon.dofus.bot.util.game.InteractiveUtil
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.io.MouseUtil
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import kotlin.math.max
import kotlin.math.min

class NpcSpeakTask(private val npcId: Int, private val optionIds: List<Int>) : DofusBotTask<Boolean>() {

    companion object {
        private val TOP_OPTION_LOCATION = PointRelative(0.36847493f, 0.6508951f)
        private val BOTTOM_OPTION_LOCATION = PointRelative(0.36847493f, 0.7442455f)
        private val DELTA_OPTION = (BOTTOM_OPTION_LOCATION.y - TOP_OPTION_LOCATION.y) / 4f
        private const val MAX_OPTION_COUNT = 5
    }

    override fun execute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        WaitUtil.sleep(1500)
        val npcLocation = InteractiveUtil.getNpcClickPosition(gameInfo, npcId)
        gameInfo.eventStore.clear()
        MouseUtil.leftClick(gameInfo, npcLocation, 0)
        WaitUtil.waitForEvent(gameInfo, NpcDialogCreationMessage::class.java)
        for (optionId in optionIds) {
            WaitUtil.waitForEvents(gameInfo, NpcDialogQuestionMessage::class.java, BasicNoOperationMessage::class.java)
            WaitUtil.sleep(300)
            val dialogQuestionMessage = gameInfo.eventStore.getLastEvent(NpcDialogQuestionMessage::class.java)
                ?: error("Missing dialog question message")
            val visibleReplies = dialogQuestionMessage.visibleReplies
            val showMorePressCount = getShowMorePressCount(optionId, visibleReplies)
            for (i in 0 until showMorePressCount) {
                MouseUtil.leftClick(gameInfo, BOTTOM_OPTION_LOCATION, 0)
                WaitUtil.sleep(300)
            }
            val displayedReplies = visibleReplies.subList(showMorePressCount * 4, visibleReplies.size)
            val optionIndex = if (optionId < 0) 0 else displayedReplies.indexOf(optionId)
            if (optionIndex == -1) {
                error("Missing option ID : $optionId")
            }
            val optionCount = max(1, min(MAX_OPTION_COUNT, dialogQuestionMessage.visibleReplies.size))
            val optionLocation =
                BOTTOM_OPTION_LOCATION.getSum(PointRelative(0f, (optionIndex - optionCount + 1) * DELTA_OPTION))
            gameInfo.eventStore.clear()
            MouseUtil.leftClick(gameInfo, optionLocation, 0)
        }
        WaitUtil.waitForEvents(gameInfo, LeaveDialogMessage::class.java, BasicNoOperationMessage::class.java)
        gameInfo.eventStore.clearUntilLast(LeaveDialogMessage::class.java)
        return true
    }

    private fun getShowMorePressCount(optionId: Int, visibleReplies: List<Int>): Int {
        val optionIndex = visibleReplies.indexOf(optionId)
        if (visibleReplies.size <= 5 || optionIndex < 5) {
            return 0
        }
        return 1 + getShowMorePressCount(optionId, visibleReplies.subList(5, visibleReplies.size))
    }

    override fun onStarted(): String {
        return "Interacting with NPC $npcId ..."
    }
}