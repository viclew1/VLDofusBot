package fr.lewon.dofus.bot.scripts.smithmagic

enum class SmithMagicCharacteristics(
    val keyWord: String,
    val normalWeight: Float,
    val paWeight: Float? = null,
    val raWeight: Float? = null
) {

    VITALITY("vitality", 1f, 3f, 10f),
    EARTH_PER_RES("airElementResistPercent", 6f),
    FIRE_PER_RES("waterElementResistPercent", 6f),
    WATER_PER_RES("fireElementResistPercent", 6f),
    AIR_PER_RES("neutralElementResistPercent", 6f),
    NEUTRAL_PER_RES("earthElementResistPercent", 6f),
    AP("actionPoints", 100f),

}