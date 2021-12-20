package fr.lewon.dofus.bot.scripts.tasks.impl.transport

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.sniffer.model.messages.move.SetCharacterRestrictionsMessage
import fr.lewon.dofus.bot.util.network.GameInfo

class LeaveHavenBagTask : AbstractHavenBagTask(false) {

    override fun doExecute(logItem: LogItem, gameInfo: GameInfo): Boolean {
        if (super.doExecute(logItem, gameInfo)) {
            val eventStore = gameInfo.eventStore
            gameInfo.playerId = eventStore.getFirstEvent(SetCharacterRestrictionsMessage::class.java)?.actorId
                ?: error("Player ID not found")
            gameInfo.logger.addSubLog("Player ID is : ${gameInfo.playerId}", logItem)
            return true
        }
        return false
    }

    override fun onStarted(): String {
        return "Leaving haven bag ..."
    }
}