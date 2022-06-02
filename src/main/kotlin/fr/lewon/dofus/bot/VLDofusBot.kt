package fr.lewon.dofus.bot

import fr.lewon.dofus.bot.gui.init.InitFrame
import fr.lewon.dofus.bot.util.io.WaitUtil


class VLDofusBot

fun main() {
    InitFrame.isResizable = false
    InitFrame.isUndecorated = true
    InitFrame.setLocationRelativeTo(null)
    InitFrame.isVisible = true
    WaitUtil.sleep(500)
    InitFrame.startInit()
}