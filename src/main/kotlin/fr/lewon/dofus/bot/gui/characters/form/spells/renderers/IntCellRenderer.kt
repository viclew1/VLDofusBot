package fr.lewon.dofus.bot.gui.characters.form.spells.renderers

import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import java.awt.Component
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.TableCellRenderer

class IntCellRenderer : TableCellRenderer {

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val editor = IntegerJTextField()
        editor.background = if (isSelected) table.selectionBackground else table.background
        editor.horizontalAlignment = SwingConstants.CENTER
        if (value is Int || value is String && value.toIntOrNull() != null) {
            editor.text = value.toString()
        } else {
            editor.text = "0"
        }
        return editor
    }
}