package fr.lewon.dofus.bot.core.ui

import fr.lewon.dofus.bot.core.ui.xml.containers.Container

object UIBounds {

    const val TOTAL_WIDTH = 1280f
    const val TOTAL_HEIGHT = 1024f
    val CENTER = UIPoint(TOTAL_WIDTH / 2f, TOTAL_HEIGHT / 2f)

    fun buildRootContainer(): Container {
        return Container("ROOT").also {
            it.defaultTopLeftPosition = UIPoint()
            it.defaultSize = UIPoint(TOTAL_WIDTH, TOTAL_HEIGHT)
        }
    }
}