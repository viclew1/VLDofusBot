package fr.lewon.dofus.bot.core.d2o.managers.quest

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.quest.DofusQuest

object QuestManager : VldbManager {

    private lateinit var questById: Map<Int, DofusQuest>

    override fun initManager() {
        questById = D2OUtil.getObjects("Quests").associate {
            val id = it["id"].toString().toInt()
            val startCriterion = it["startCriterion"].toString()
            val stepIds = it["stepIds"] as List<Int>
            val steps = stepIds.map { stepId -> QuestStepManager.getQuestStep(stepId) }
            id to DofusQuest(id, startCriterion, steps)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(QuestStepManager)
    }

    fun getQuest(id: Int): DofusQuest {
        return questById[id] ?: error("No quest for id : $id")
    }

}