package fr.lewon.dofus.bot.core.model.spell

data class DofusSpell(
    val id: Int,
    val iconId: Int,
    val name: String,
    val levels: List<DofusSpellLevel>
)