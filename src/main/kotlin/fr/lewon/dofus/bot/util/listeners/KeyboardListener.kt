package fr.lewon.dofus.bot.util.listeners

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import fr.lewon.dofus.bot.gui.overlay.AbstractOverlay
import fr.lewon.dofus.bot.gui.overlay.LOSHelper
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

object KeyboardListener : Thread(), NativeKeyListener {

    private val keysPressed = HashMap<Int, Boolean>()
    private val keysByOverlay = mapOf(
        LOSHelper to listOf(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_X)
    ).toMap()
    private var displayedOverlay: AbstractOverlay? = null

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
        keysPressed[e.keyCode] = true
        if (keysPressed[NativeKeyEvent.VC_K] == true && keysPressed[NativeKeyEvent.VC_CONTROL] == true) {
            ScriptRunner.stopScript()
        }
        toggleOverlay()
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        keysPressed[e.keyCode] = false
        toggleOverlay()
    }

    private fun toggleOverlay() {
        val newDisplayedOverlay = keysByOverlay.entries.firstOrNull { shouldDisplayOverlay(it.value) }?.key
        if (newDisplayedOverlay != null && newDisplayedOverlay != displayedOverlay) {
            val foregroundGamePID = GameSnifferUtil.getAllPIDs().firstOrNull()
            if (foregroundGamePID != null) {
                newDisplayedOverlay.updateOverlay(foregroundGamePID)
                displayedOverlay = newDisplayedOverlay
            }
        } else if (newDisplayedOverlay == null) {
            displayedOverlay = null
        }
        keysByOverlay.keys.forEach { it.isVisible = displayedOverlay == it }
    }

    private fun shouldDisplayOverlay(nativeKeyEvents: List<Int>): Boolean {
        return nativeKeyEvents.map { keysPressed[it] ?: false }.firstOrNull { !it } == null
    }
}