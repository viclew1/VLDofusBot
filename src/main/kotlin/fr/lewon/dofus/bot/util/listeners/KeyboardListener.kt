package fr.lewon.dofus.bot.util.listeners

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import fr.lewon.dofus.bot.util.io.SystemKeyLock
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

object KeyboardListener : Thread(), NativeKeyListener {

    private val keysPressed = HashSet<Int>()
    private var modifierPressed = false
    private val lock = ReentrantLock(true)

    override fun run() {
        LogManager.getLogManager().reset()
        val logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
        logger.level = Level.OFF
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(this)
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) {}

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        try {
            lock.lockInterruptibly()
            keysPressed.add(e.keyCode)
            if (!modifierPressed && e.modifiers != 0) {
                modifierPressed = true
                SystemKeyLock.lockInterruptibly()
            }
            toggleOverlays()
        } finally {
            lock.unlock()
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        try {
            lock.lockInterruptibly()
            keysPressed.remove(e.keyCode)
            if (modifierPressed && e.modifiers == 0) {
                modifierPressed = false
                SystemKeyLock.unlock()
            }
        } finally {
            lock.unlock()
        }
    }

    private fun toggleOverlays() {
        OverlayInfo.values().firstOrNull { hotKeyPressed(it.keys) }?.let {
            OverlayManager.scheduleToggleOverlay(it)
        }
    }

    private fun hotKeyPressed(nativeKeyEvents: List<Int>): Boolean {
        if (keysPressed.size != nativeKeyEvents.size) {
            return false
        }
        return nativeKeyEvents.all { keysPressed.contains(it) }
    }
}