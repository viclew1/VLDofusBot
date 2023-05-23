package fr.lewon.dofus.bot.core.model.item

data class DofusItemEffect(
    val min: Int,
    val max: Int,
    val effect: DofusEffect
) {
    val realMaxValue = (if (effect.operator == "-") -min else max).takeIf { it != 0 }
    val realMinValue = (if (effect.operator == "-") -max else min).takeIf { it != 0 }
}