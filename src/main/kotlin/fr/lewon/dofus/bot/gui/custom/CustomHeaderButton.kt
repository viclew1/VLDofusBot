package fr.lewon.dofus.bot.gui.custom

import java.awt.Color
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.SwingConstants

class CustomHeaderButton(title: String, onClick: (MouseEvent) -> Unit) : JLabel(title) {

    init {
        verticalAlignment = SwingConstants.BOTTOM
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                onClick.invoke(e)
            }

            override fun mouseEntered(e: MouseEvent) {
                foreground = Color.BLACK
            }

            override fun mouseExited(e: MouseEvent) {
                foreground = Color.WHITE
            }
        })
        horizontalAlignment = SwingConstants.CENTER
        verticalAlignment = SwingConstants.TOP
        foreground = Color.WHITE
        font = Font("Tahoma", Font.PLAIN, 20)
    }

}