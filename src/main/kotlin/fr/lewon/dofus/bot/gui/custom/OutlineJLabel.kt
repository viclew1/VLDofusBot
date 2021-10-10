package fr.lewon.dofus.bot.gui.custom

import java.awt.Color
import java.awt.Graphics
import javax.swing.JLabel


class OutlineJLabel(text: String = "", var outlineColor: Color = Color.BLACK) : JLabel(text) {

    private var forceTransparent: Boolean = false
    private var isPaintingOutline: Boolean = false

    override fun getForeground(): Color? {
        return if (isPaintingOutline) {
            outlineColor
        } else {
            super.getForeground()
        }
    }

    override fun isOpaque(): Boolean {
        return if (forceTransparent) {
            false
        } else {
            super.isOpaque()
        }
    }

    override fun paint(g: Graphics) {
        val text = text
        if (text == null || text.isEmpty()) {
            super.paint(g)
            return
        }

        // 1 2 3
        // 8 9 4
        // 7 6 5
        if (isOpaque) super.paint(g)
        forceTransparent = true
        isPaintingOutline = true
        g.translate(-1, -1)
        super.paint(g) // 1
        g.translate(1, 0)
        super.paint(g) // 2
        g.translate(1, 0)
        super.paint(g) // 3
        g.translate(0, 1)
        super.paint(g) // 4
        g.translate(0, 1)
        super.paint(g) // 5
        g.translate(-1, 0)
        super.paint(g) // 6
        g.translate(-1, 0)
        super.paint(g) // 7
        g.translate(0, -1)
        super.paint(g) // 8
        g.translate(1, 0) // 9
        isPaintingOutline = false
        super.paint(g)
        forceTransparent = false
    }

}