package fr.lewon.dofus.bot.core.model.item

import fr.lewon.dofus.bot.core.model.charac.DofusCharacteristic

data class DofusEffect(
    val id: Int,
    val description: String,
    val characteristic: DofusCharacteristic?,
    val useDice: Boolean,
    val boost: Boolean,
    val active: Boolean,
    val operator: String
)