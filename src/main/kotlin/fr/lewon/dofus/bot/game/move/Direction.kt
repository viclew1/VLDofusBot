package fr.lewon.dofus.bot.game.move

import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*


enum class Direction(
    private val directionInt: Int,
    private val moveServiceFetcher: () -> MoveTask,
    private val reverseDirFetcher: () -> Direction
) {

    LEFT(4, { MoveLeftTask() }, { RIGHT }),
    RIGHT(0, { MoveRightTask() }, { LEFT }),
    BOTTOM(2, { MoveBottomTask() }, { TOP }),
    TOP(6, { MoveTopTask() }, { BOTTOM });

    fun buildMoveTask(): MoveTask {
        return moveServiceFetcher.invoke()
    }

    fun getReverseDir(): Direction {
        return reverseDirFetcher.invoke()
    }

    companion object {
        fun fromInt(directionInt: Int): Direction? {
            return values().firstOrNull { it.directionInt == directionInt }
        }
    }

}