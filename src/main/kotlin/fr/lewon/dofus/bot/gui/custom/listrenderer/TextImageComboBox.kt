package fr.lewon.dofus.bot.gui.custom.listrenderer

import javax.swing.JComboBox

open class TextImageComboBox<T : TextImageListItem>(imageSize: Int, values: Array<T>) : JComboBox<T>(values) {

    init {
        setRenderer(TextImageListCellRenderer(imageSize, values))
    }

}