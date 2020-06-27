package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.scripts.tasks.impl.moves.*
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem


enum class Directions(
    private val moveServiceFetcher: (DofusTreasureBotGUIController, LogItem?) -> MoveTask,
    private val reverseDirFetcher: () -> Directions
) {

    LEFT({ c, l -> MoveLeftTask(c, l) }, { RIGHT }),
    RIGHT({ c, l -> MoveRightTask(c, l) }, { LEFT }),
    BOTTOM({ c, l -> MoveBottomTask(c, l) }, { TOP }),
    TOP({ c, l -> MoveTopTask(c, l) }, { BOTTOM });

    fun buildMoveTask(controller: DofusTreasureBotGUIController, logItem: LogItem?): MoveTask {
        return moveServiceFetcher.invoke(controller, logItem)
    }

    fun getReverseDir(): Directions {
        return reverseDirFetcher.invoke()
    }

}