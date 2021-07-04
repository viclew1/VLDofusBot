package fr.lewon.dofus.bot

import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.sniffer.SocketListener
import fr.lewon.dofus.bot.util.WindowsUtil
import fr.lewon.dofus.bot.util.listeners.KeyboardListener
import nu.pattern.OpenCV


class DofusTreasureBotFXApp {

}

fun main() {
    OpenCV.loadLocally()
    WindowsUtil.updateGameBounds()
    WindowsUtil.bringGameToFront()
    SocketListener.start()
    KeyboardListener.start()

    val jf = MainFrame
    jf.isResizable = false
    jf.isUndecorated = true
    jf.isVisible = true
}