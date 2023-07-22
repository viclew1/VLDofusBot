package fr.lewon.dofus.bot.core.model.entity

import fr.lewon.dofus.bot.core.model.charac.DofusCharacteristic
import fr.lewon.dofus.bot.core.model.spell.DofusSpell

data class DofusMonster(
    val id: Double,
    val name: String,
    val isMiniBoss: Boolean,
    val isBoss: Boolean,
    val isQuestMonster: Boolean,
    val spells: List<DofusSpell>,
    val baseStats: Map<DofusCharacteristic, Int>,
    val useSummonSlot: Boolean,
    val canSwitchPos: Boolean,
    val canSwitchPosOnTarget: Boolean,
    val canBePushed: Boolean,
)