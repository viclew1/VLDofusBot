package fr.lewon.dofus.bot.core.model.spell

enum class DofusEffectZoneType(val zoneTypeKey: Char) {

    POINT('P'),
    CIRCLE('C'),
    CROSS('X'),
    CROSS_FROM_TARGET('Q'),
    DIAGONAL_CROSS('+'),
    LINE('L'),
    PERPENDICULAR_LINE('T'),
    CONE('V');

    companion object {
        fun fromKey(zoneTypeKey: Char): DofusEffectZoneType? {
            return values().firstOrNull { it.zoneTypeKey == zoneTypeKey }
        }
    }

}