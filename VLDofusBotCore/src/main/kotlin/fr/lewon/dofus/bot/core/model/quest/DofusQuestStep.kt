package fr.lewon.dofus.bot.core.model.quest

data class DofusQuestStep(
    val id: Int,
    val objectiveIds: List<Int>
)