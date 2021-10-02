package fr.lewon.dofus.bot.gui.characters.form.spells.renderers

import fr.lewon.dofus.bot.gui.custom.CustomJTextField
import java.awt.Component
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.TableCellRenderer

class StringCellRenderer : TableCellRenderer {

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val editor = CustomJTextField()
        editor.background = if (isSelected) table.selectionBackground else table.background
        editor.horizontalAlignment = SwingConstants.CENTER
        editor.text = value?.toString() ?: ""
        return editor
    }
}