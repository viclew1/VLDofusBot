package fr.lewon.dofus.bot

import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.sniffer.DofusMessageReceiver
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.sniffer.store.EventStore
import fr.lewon.dofus.bot.util.WindowsUtil
import fr.lewon.dofus.bot.util.listeners.KeyboardListener
import nu.pattern.OpenCV
import org.reflections.Reflections

class VLDofusBot

fun main() {
    OpenCV.loadLocally()
    WindowsUtil.updateGameBounds()
    DofusMessageReceiver.start()
    initEventStoreHandlers()
    KeyboardListener.start()

    val jf = MainFrame
    jf.isResizable = false
    jf.isUndecorated = true
    jf.isVisible = true
}

fun initEventStoreHandlers() {
    Reflections(VLDofusBot::class.java.packageName)
        .getSubTypesOf(EventHandler::class.java)
        .filter { !it.kotlin.isAbstract }
        .mapNotNull { it.kotlin.objectInstance ?: it.getConstructor().newInstance() }
        .forEach {
            EventStore.addEventHandler(it)
        }
}