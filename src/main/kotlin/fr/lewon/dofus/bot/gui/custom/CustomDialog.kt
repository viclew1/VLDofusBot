package fr.lewon.dofus.bot.gui.custom

import java.awt.Color
import javax.swing.JDialog
import javax.swing.JFrame

open class CustomDialog(
    title: String,
    w: Int,
    h: Int,
    headerColor: Color,
    val headerHeight: Int,
    owner: JFrame
) : JDialog(owner) {

    init {
        CustomWindowBuilder.initCustomWindow(
            this, getRootPane(), contentPane, title, w, h, headerHeight,
            headerColor, listOf(CustomHeaderButton("x") { dispose() })
        )
        isUndecorated = true
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
    }

}