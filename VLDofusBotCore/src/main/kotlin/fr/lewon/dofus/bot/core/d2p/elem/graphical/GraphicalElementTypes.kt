package fr.lewon.dofus.bot.core.d2p.elem.graphical

enum class GraphicalElementTypes(val typeInt: Int) {
    NORMAL(0),
    BOUNDING_BOX(1),
    ANIMATED(2),
    ENTITY(3),
    PARTICLES(4),
    BLENDED(5);

    companion object {

        fun fromInt(typeInt: Int): GraphicalElementTypes? {
            return entries.firstOrNull { it.typeInt == typeInt }
        }
    }
}