package fr.lewon.dofus.bot.core.model.quest

data class DofusQuest(
    val id: Int,
    val startCriterion: String,
    val steps: List<DofusQuestStep>,
)