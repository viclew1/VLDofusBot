package fr.lewon.dofus.bot.core.world

enum class TransitionType(val typeInt: Int) {
    UNSPECIFIED(0),
    SCROLL(1),
    SCROLL_ACTION(2),
    MAP_EVENT(4),
    MAP_ACTION(8),
    MAP_OBSTACLE(16),
    INTERACTIVE(32),
    NPC_ACTION(64);

    companion object {
        fun fromInt(type: Int): TransitionType {
            return values().firstOrNull { type == it.typeInt }
                ?: error("Type not found : $type")
        }
    }
}