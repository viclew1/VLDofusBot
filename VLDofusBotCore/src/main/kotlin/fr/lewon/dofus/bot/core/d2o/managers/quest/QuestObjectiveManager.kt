package fr.lewon.dofus.bot.core.d2o.managers.quest

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.model.quest.DofusQuestObjective

object QuestObjectiveManager : VldbManager {

    private lateinit var questObjectiveById: Map<Int, DofusQuestObjective>

    override fun initManager() {
        questObjectiveById = D2OUtil.getObjects("QuestObjectives").associate {
            val id = it["id"].toString().toInt()
            val mapId = it["mapId"].toString().toDouble()
            val stepId = it["stepId"].toString().toInt()
            val step = QuestStepManager.getQuestStep(stepId)
            id to DofusQuestObjective(id, step, mapId)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return listOf(QuestStepManager)
    }

    fun getQuestObjective(id: Int): DofusQuestObjective {
        return questObjectiveById[id] ?: error("No quest objective for id : $id")
    }

}