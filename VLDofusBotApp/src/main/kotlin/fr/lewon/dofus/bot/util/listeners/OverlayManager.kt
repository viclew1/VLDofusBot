package fr.lewon.dofus.bot.util.listeners

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharactersUIUtil
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.locks.ReentrantLock

object OverlayManager {

    private var displayedOverlay: OverlayInfo? = null
    private val lock = ReentrantLock()
    private val toToggleOverlays = LinkedBlockingDeque<OverlayInfo>()
    private val overlayManagerTimer = Timer()

    fun scheduleToggleOverlay(toToggleOverlay: OverlayInfo) {
        toToggleOverlays.add(toToggleOverlay)
        overlayManagerTimer.schedule(object : TimerTask() {
            override fun run() {
                lock.executeSyncOperation {
                    toggleOverlay(toToggleOverlays.pollFirst())
                }
            }
        }, 0)
    }

    private fun toggleOverlay(toToggleOverlay: OverlayInfo) {
        if (toToggleOverlay == displayedOverlay) {
            toToggleOverlay.overlay.isVisible = false
            displayedOverlay = null
        } else if (GlobalConfigManager.readConfig().run { displayOverlays && shouldDisplayOverlay(toToggleOverlay) }) {
            val characterUIState = CharactersUIUtil.getSelectedCharacterUIState() ?: return
            val character = CharacterManager.getCharacter(characterUIState.value.name) ?: return
            val connection = GameSnifferUtil.getFirstConnection(character) ?: return
            toToggleOverlay.overlay.updateOverlay(GameSnifferUtil.getGameInfoByConnection(connection))
            displayedOverlay?.overlay?.isVisible = false
            toToggleOverlay.overlay.isVisible = true
            displayedOverlay = toToggleOverlay
        }
    }
}