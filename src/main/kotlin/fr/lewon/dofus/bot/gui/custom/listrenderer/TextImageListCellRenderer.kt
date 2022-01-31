package fr.lewon.dofus.bot.gui.custom.listrenderer

import fr.lewon.dofus.bot.gui.util.ImageUtil
import java.awt.Component
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class TextImageListCellRenderer<T : TextImageListItem>(
    imageSize: Int, values: Array<T>
) : JLabel(), ListCellRenderer<T> {

    private val images = values.map { ImageUtil.getScaledImage(it.getListImageIconData(), imageSize, imageSize) }
    private val texts = values.map { it.getLabel() }

    init {
        isOpaque = true
    }

    override fun getListCellRendererComponent(
        list: JList<out T>, value: T, index: Int, isSelected: Boolean, cellHasFocus: Boolean
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

    private fun render(value: T) {
        val selectedIndex = value.getIndex()
        icon = ImageIcon(images[selectedIndex])
        text = texts[selectedIndex]
    }

}