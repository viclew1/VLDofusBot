package fr.lewon.dofus.bot.gui.custom

import fr.lewon.dofus.bot.gui.custom.ext.ComponentResizer
import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame


open class CustomFrame(
    title: String,
    w: Int,
    h: Int,
    headerColor: Color,
    val headerHeight: Int,
    iconImageData: ByteArray? = null,
    reduceButton: Boolean = true,
    closeButton: Boolean = true
) : JFrame(title) {

    init {
        val buttons = ArrayList<CustomHeaderButton>()
        if (reduceButton) buttons.add(CustomHeaderButton("-") { state = ICONIFIED })
        if (closeButton) buttons.add(CustomHeaderButton("x") { dispose() })

        CustomWindowBuilder.initCustomWindow(
            this, getRootPane(), contentPane, title, w, h, headerHeight,
            headerColor, buttons, iconImageData
        )
        isUndecorated = true
        defaultCloseOperation = EXIT_ON_CLOSE

        val cr = ComponentResizer()
        cr.registerComponent(this)
        cr.snapSize = Dimension(10, 10)
        cr.minimumSize = Dimension(600, 400)
    }

}