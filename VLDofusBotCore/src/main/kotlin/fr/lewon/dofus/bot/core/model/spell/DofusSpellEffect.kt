package fr.lewon.dofus.bot.core.model.spell

data class DofusSpellEffect(
    var min: Int,
    var max: Int,
    var rawZone: DofusEffectZone,
    var effectType: DofusSpellEffectType,
    var targets: List<DofusSpellTarget>
)