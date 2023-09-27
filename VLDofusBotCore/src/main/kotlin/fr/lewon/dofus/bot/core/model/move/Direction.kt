package fr.lewon.dofus.bot.core.model.move

enum class Direction(val directionInt: Int) {

    LEFT(4),
    RIGHT(0),
    BOTTOM(2),
    TOP(6);

    companion object {

        fun fromInt(directionInt: Int): Direction {
            return entries.firstOrNull { it.directionInt == directionInt }
                ?: error("Direction [$directionInt] not found")
        }
    }

}