package fr.lewon.dofus.bot.gui.custom

import java.awt.Color
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URL
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants

open class CustomFrame(
    title: String,
    w: Int,
    h: Int,
    headerColor: Color,
    val headerHeight: Int,
    iconUrl: URL? = null,
    reduceButton: Boolean = true,
    closeButton: Boolean = true
) : JFrame(title) {

    init {
        val buttons = ArrayList<CustomHeaderButton>()
        if (reduceButton) buttons.add(CustomHeaderButton("-") { state = ICONIFIED })
        if (closeButton) buttons.add(CustomHeaderButton("x") { dispose() })
        CustomWindowBuilder.initCustomWindow(
            this, getRootPane(), contentPane, title, w, h, headerHeight,
            headerColor, buttons, iconUrl
        )

        if (reduceButton) {
            val lblReduce = JLabel("-")
            lblReduce.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    state = ICONIFIED
                }

                override fun mouseEntered(e: MouseEvent) {
                    lblReduce.foreground = Color.BLACK
                }

                override fun mouseExited(e: MouseEvent) {
                    lblReduce.foreground = Color.WHITE
                }
            })
            lblReduce.horizontalAlignment = SwingConstants.CENTER
            lblReduce.verticalAlignment = SwingConstants.TOP
            lblReduce.foreground = Color.WHITE
            lblReduce.font = Font("Tahoma", Font.PLAIN, 20)
            lblReduce.setBounds(size.width - 2 * 30, 0, 30, 30)
            contentPane.add(lblReduce, 0)
        }

        isUndecorated = true
        defaultCloseOperation = EXIT_ON_CLOSE
    }

}