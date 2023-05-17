package fr.lewon.dofus.bot.core.d2o.managers.quest

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.quest.DofusQuestStep

object QuestStepManager : VldbManager {

    private lateinit var questStepById: Map<Int, DofusQuestStep>

    override fun initManager() {
        questStepById = D2OUtil.getObjects("QuestSteps").associate {
            val id = it["id"].toString().toInt()
            val objectiveIds = it["objectiveIds"] as List<Int>
            id to DofusQuestStep(id, objectiveIds)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getQuestStep(id: Int): DofusQuestStep {
        return questStepById[id] ?: error("No quest step for id : $id")
    }

}