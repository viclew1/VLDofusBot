package fr.lewon.dofus.bot.model.characters.sets

data class CharacterSets(
    var characterName: String = "",
    var selectedSetName: String = "",
    var sets: List<CharacterSet> = emptyList(),
) {

    fun getSelectedSet(): CharacterSet? = sets.firstOrNull { it.name == selectedSetName }
}