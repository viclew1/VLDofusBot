package fr.lewon.dofus.bot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.gui.init.InitPanel
import org.slf4j.LoggerFactory

class VLDofusBot

fun main() {

    val jf = MainFrame
    jf.isResizable = false
    jf.isUndecorated = true
    jf.isVisible = true

    initAll()
}

fun initAll() {
    val root = LoggerFactory.getLogger("org.reflections") as Logger
    root.level = Level.OFF

    MainFrame.startInit()
    InitPanel.initAll()
    MainFrame.stopInit()
}