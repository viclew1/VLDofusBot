package fr.lewon.dofus.bot.gui.panes.character.dofusclass

import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.DofusClass
import java.awt.Component
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class DofusClassRenderer : JLabel(), ListCellRenderer<DofusClass> {

    private val images = DofusClass.values().map { ImageUtil.getScaledImage(it.simpleIconData, 25, 25) }
    private val texts = DofusClass.values().map { it.classLabel }

    init {
        isOpaque = true
    }

    override fun getListCellRendererComponent(
        list: JList<out DofusClass>,
        value: DofusClass,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        render(value)
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

    private fun render(value: DofusClass) {
        val selectedIndex = value.ordinal
        icon = ImageIcon(images[selectedIndex])
        text = texts[selectedIndex]
    }

}