package fr.lewon.dofus.bot.util.listeners

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import fr.lewon.dofus.bot.util.script.ScriptRunner
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

object KeyboardListener : Thread(), NativeKeyListener {

    private val keysPressed = HashMap<Int, Boolean>()

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
        if (keysPressed[NativeKeyEvent.VC_C] == true && keysPressed[NativeKeyEvent.VC_CONTROL] == true) {
            ScriptRunner.stopScript()
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        keysPressed[e.keyCode] = false
    }
}