package fr.lewon.dofus.bot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import fr.lewon.dofus.bot.gui.init.InitFrame
import org.slf4j.LoggerFactory

class VLDofusBot

fun main() {

    InitFrame.isResizable = false
    InitFrame.isUndecorated = true
    InitFrame.setLocationRelativeTo(null)
    InitFrame.isVisible = true

    initAll()
}

fun initAll() {
    val root = LoggerFactory.getLogger("org.reflections") as Logger
    root.level = Level.OFF
    InitFrame.startInit()
}