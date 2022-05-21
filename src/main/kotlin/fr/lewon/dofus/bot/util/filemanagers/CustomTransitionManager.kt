package fr.lewon.dofus.bot.util.filemanagers

import fr.lewon.dofus.bot.core.world.WorldGraphUtil

object CustomTransitionManager {

    private val TO_DISABLE_TRANSITION_IDS = listOf(
        519634.0
    )

    fun initManager() {
        TO_DISABLE_TRANSITION_IDS.forEach {
            WorldGraphUtil.addInvalidTransitionId(it)
        }
    }

}