package fr.lewon.dofus.bot.util.listeners

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.main.characters.CharactersUIUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.util.concurrent.locks.ReentrantLock

object OverlayManager {

    private var displayedOverlay: OverlayInfo? = null
    private val lock = ReentrantLock()

    fun toggleOverlay(toToggleOverlay: OverlayInfo) {
        Thread {
            lock.executeSyncOperation {
                doToggleOverlay(toToggleOverlay)
            }
        }.start()
    }

    private fun doToggleOverlay(toToggleOverlay: OverlayInfo) {
        if (toToggleOverlay == displayedOverlay) {
            toToggleOverlay.overlay.isVisible = false
            displayedOverlay = null
        } else if (GlobalConfigManager.readConfig().run { displayOverlays && shouldDisplayOverlay(toToggleOverlay) }) {
            val characterUIStates = CharactersUIUtil.getSelectedCharactersUIStates()
            if (characterUIStates.size != 1) {
                println("Select exactly one character to display an overlay")
            } else {
                val character = CharacterManager.getCharacter(characterUIStates.first().name) ?: return
                val connection = GameSnifferUtil.getFirstConnection(character) ?: return
                toToggleOverlay.overlay.updateOverlay(GameSnifferUtil.getGameInfoByConnection(connection))
                displayedOverlay?.overlay?.isVisible = false
                toToggleOverlay.overlay.isVisible = true
                displayedOverlay = toToggleOverlay
            }
        }
    }
}