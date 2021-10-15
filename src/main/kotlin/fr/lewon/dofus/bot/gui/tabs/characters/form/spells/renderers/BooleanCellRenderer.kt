package fr.lewon.dofus.bot.gui.tabs.characters.form.spells.renderers

import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.TableCellRenderer

class BooleanCellRenderer : TableCellRenderer {

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val editor = JCheckBox()
        editor.background = if (isSelected) table.selectionBackground else table.background
        editor.horizontalAlignment = SwingConstants.CENTER
        if (value != null && value is Boolean) {
            editor.isSelected = value
        } else {
            editor.isSelected = false
        }
        return editor
    }
}