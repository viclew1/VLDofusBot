package fr.lewon.dofus.bot.gui.vldb.panes.config

import net.miginfocom.swing.MigLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSeparator

abstract class AbstractConfigPanel : JPanel(MigLayout()) {

    protected fun addLine(leftComponent: JComponent, rightComponent: JComponent, separator: Boolean = true) {
        add(leftComponent)
        add(rightComponent, "width 80:80:80, al right, wrap")
        if (separator) addSeparator()
    }

    private fun addSeparator() {
        add(JSeparator(JSeparator.HORIZONTAL), "span 2 1, width max, wrap")
    }

}