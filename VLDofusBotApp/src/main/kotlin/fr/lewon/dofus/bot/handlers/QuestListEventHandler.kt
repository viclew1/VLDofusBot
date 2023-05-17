package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.core.d2o.managers.quest.QuestManager
import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.game.context.roleplay.quest.QuestListMessage
import fr.lewon.dofus.bot.sniffer.model.types.game.context.roleplay.quest.QuestActiveDetailedInformations
import fr.lewon.dofus.bot.sniffer.store.IEventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object QuestListEventHandler : IEventHandler<QuestListMessage> {
    override fun onEventReceived(socketResult: QuestListMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.finishedQuestIds = socketResult.finishedQuestsIds
        gameInfo.activeQuestIds = socketResult.activeQuests.map { it.questId }

        val activeQuestsObjectiveIds = socketResult.activeQuests
            .filterIsInstance(QuestActiveDetailedInformations::class.java)
            .flatMap { it.objectives }
        val activeQuestsFinishedObjectiveIds = activeQuestsObjectiveIds
            .filter { !it.objectiveStatus }
            .map { it.objectiveId }
        val finishedQuestsObjectiveIds = socketResult.finishedQuestsIds
            .map { QuestManager.getQuest(it) }
            .flatMap { it.steps }
            .flatMap { it.objectiveIds }
        val finishedObjectiveIds = ArrayList<Int>()
        finishedObjectiveIds.addAll(finishedQuestsObjectiveIds)
        finishedObjectiveIds.addAll(activeQuestsFinishedObjectiveIds)
        gameInfo.finishedObjectiveIds = finishedObjectiveIds
        gameInfo.activeObjectiveIds = activeQuestsObjectiveIds.filter { it.objectiveStatus }.map { it.objectiveId }
    }
}