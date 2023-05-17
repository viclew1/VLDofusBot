package fr.lewon.dofus.bot.core.model.item

import fr.lewon.dofus.bot.core.model.charac.DofusCharacteristic

data class DofusItemEffect(
    val min: Int,
    val max: Int,
    val characteristic: DofusCharacteristic
)