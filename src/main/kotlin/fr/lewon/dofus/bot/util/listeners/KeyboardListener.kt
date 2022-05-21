package fr.lewon.dofus.bot.util.listeners

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import fr.lewon.dofus.bot.gui.overlay.AbstractOverlay
import fr.lewon.dofus.bot.gui.overlay.impl.BreedingOverlay
import fr.lewon.dofus.bot.gui.overlay.impl.GfxOverlay
import fr.lewon.dofus.bot.gui.overlay.impl.LOSOverlay
import fr.lewon.dofus.bot.gui.overlay.impl.UIOverlay
import fr.lewon.dofus.bot.gui.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.io.SystemKeyLock
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

object KeyboardListener : Thread(), NativeKeyListener {

    private val keysPressed = HashSet<Int>()
    private var modifierPressed = false
    private val keysByOverlay = mapOf(
        LOSOverlay to listOf(NativeKeyEvent.VC_L, NativeKeyEvent.VC_CONTROL),
        BreedingOverlay to listOf(NativeKeyEvent.VC_B, NativeKeyEvent.VC_CONTROL),
        UIOverlay to listOf(NativeKeyEvent.VC_U, NativeKeyEvent.VC_CONTROL),
        GfxOverlay to listOf(NativeKeyEvent.VC_G, NativeKeyEvent.VC_CONTROL),
    ).toMap()
    private var displayedOverlay: AbstractOverlay? = null
    private val lock = ReentrantLock()

    override fun run() {
        LogManager.getLogManager().reset()
        val logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
        logger.level = Level.OFF
        GlobalScreen.registerNativeHook()
        GlobalScreen.setEventDispatcher(SwingDispatchService())
        GlobalScreen.addNativeKeyListener(this)
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) {}

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        lock.lockInterruptibly()
        keysPressed.add(e.keyCode)
        if (!modifierPressed && e.modifiers != 0) {
            modifierPressed = true
            SystemKeyLock.lockInterruptibly()
        }
        toggleOverlays()
        lock.unlock()
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        lock.lockInterruptibly()
        keysPressed.remove(e.keyCode)
        if (modifierPressed && e.modifiers == 0) {
            modifierPressed = false
            SystemKeyLock.unlock()
        }
        lock.unlock()
    }

    private fun toggleOverlays() {
        val toToggleOverlay = keysByOverlay.entries.firstOrNull { hotKeyPressed(it.value) }?.key
        if (toToggleOverlay != null) {
            if (toToggleOverlay == displayedOverlay) {
                toToggleOverlay.isVisible = false
                displayedOverlay = null
            } else if (ConfigManager.config.displayOverlays) {
                val character = CharacterSelectionPanel.cardList.selectedItem ?: return
                val connection = GameSnifferUtil.getFirstConnection(character) ?: return
                toToggleOverlay.updateOverlay(GameSnifferUtil.getGameInfoByConnection(connection))
                displayedOverlay?.isVisible = false
                toToggleOverlay.isVisible = true
                displayedOverlay = toToggleOverlay
            }
        }
    }

    private fun hotKeyPressed(nativeKeyEvents: List<Int>): Boolean {
        if (keysPressed.size != nativeKeyEvents.size) {
            return false
        }
        return nativeKeyEvents.all { keysPressed.contains(it) }
    }
}