package fr.lewon.dofus.bot

import fr.lewon.dofus.bot.gui.init.InitFrame

class VLDofusBot

fun main() {

    InitFrame.isResizable = false
    InitFrame.isUndecorated = true
    InitFrame.setLocationRelativeTo(null)
    InitFrame.isVisible = true

    initAll()
}

fun initAll() {
    InitFrame.startInit()
}