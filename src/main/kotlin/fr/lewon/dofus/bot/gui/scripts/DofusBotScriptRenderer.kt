package fr.lewon.dofus.bot.gui.scripts

import fr.lewon.dofus.bot.scripts.DofusBotScript
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class DofusBotScriptRenderer : JLabel(), ListCellRenderer<DofusBotScript> {

    init {
        isOpaque = true
    }

    override fun getListCellRendererComponent(
        list: JList<out DofusBotScript>,
        value: DofusBotScript,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        this.text = value.name
        if (isSelected) {
            background = list.selectionBackground
            foreground = list.selectionForeground
        } else {
            background = list.background
            foreground = list.foreground
        }
        font = list.font
        return this
    }

}