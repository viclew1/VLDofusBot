package fr.lewon.dofus.bot.util.listeners

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.util.io.SystemKeyLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

object KeyboardListener : Thread(), NativeKeyListener {

    private val pressedByKey = ConcurrentHashMap<Int, Boolean>()
    private var modifierPressed = false
    private val lock = ReentrantLock(true)

    override fun run() {
        LogManager.getLogManager().reset()
        val logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
        logger.level = Level.OFF
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(this)
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) {
        toggleSystemKeyLock(e)
        pressedByKey[e.keyCode] = false
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        toggleSystemKeyLock(e)
        pressedByKey[e.keyCode] = true
        toggleOverlays()
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        toggleSystemKeyLock(e)
        pressedByKey[e.keyCode] = false
    }

    private fun toggleSystemKeyLock(e: NativeKeyEvent) = lock.executeSyncOperation {
        if (!modifierPressed && e.modifiers != 0) {
            modifierPressed = true
            SystemKeyLock.lockInterruptibly()
        } else if (modifierPressed && e.modifiers == 0) {
            modifierPressed = false
            SystemKeyLock.unlock()
        }
    }

    private fun toggleOverlays() {
        OverlayInfo.entries.firstOrNull { hotKeyPressed(it.keys) }?.let {
            OverlayManager.toggleOverlay(it)
        }
    }

    private fun hotKeyPressed(nativeKeyEvents: List<Int>): Boolean {
        return nativeKeyEvents.all { pressedByKey[it] == true }
    }
}