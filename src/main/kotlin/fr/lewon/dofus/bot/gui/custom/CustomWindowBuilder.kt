package fr.lewon.dofus.bot.gui.custom

import java.awt.*
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JRootPane
import javax.swing.border.LineBorder

object CustomWindowBuilder {

    fun initCustomWindow(
        window: Window,
        rootPane: JRootPane,
        contentPane: Container,
        title: String,
        w: Int,
        h: Int,
        headerHeight: Int,
        headerColor: Color,
        buttons: List<CustomHeaderButton> = emptyList(),
        iconImageData: ByteArray? = null
    ) {
        rootPane.border = LineBorder(headerColor, 2)
        contentPane.layout = null
        contentPane.background = headerColor
        window.size = Dimension(w, h)

        for (i in buttons.indices) {
            val button = buttons[i]
            button.setBounds(window.size.width - 30 * (buttons.size - i), 0, 30, 30)
            contentPane.add(button)
        }

        val lblBd = JLabel(title)
        addDrag(window, lblBd)

        lblBd.isOpaque = true
        lblBd.background = contentPane.background
        lblBd.foreground = Color.WHITE
        lblBd.font = Font("Times New Roman", Font.BOLD, 20)
        if (iconImageData != null) {
            lblBd.icon = ImageIcon(iconImageData)
        }
        lblBd.setBounds(0, 0, window.size.width, headerHeight)
        contentPane.add(lblBd)
    }

    private fun addDrag(window: Window, component: JComponent) {
        val dragListener = WindowDragListener(window)
        component.addMouseListener(dragListener)
        component.addMouseMotionListener(dragListener)
    }

}