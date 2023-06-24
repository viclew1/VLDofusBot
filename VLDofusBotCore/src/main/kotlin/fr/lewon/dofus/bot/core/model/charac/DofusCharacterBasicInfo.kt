package fr.lewon.dofus.bot.core.model.charac

data class DofusCharacterBasicInfo(
    val characterName: String,
    val breedId: Int,
    val finishedQuestsIds: List<Int>,
    val activeQuestsIds: List<Int>,
    val finishedObjectiveIds: List<Int>,
    val activeObjectiveIds: List<Int>,
    val states: List<Int>,
    val disabledTransitionZaapMapIds: List<Double>
)