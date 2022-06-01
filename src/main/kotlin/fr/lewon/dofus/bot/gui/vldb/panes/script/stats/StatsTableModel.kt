package fr.lewon.dofus.bot.gui.vldb.panes.script.stats

import javax.swing.table.DefaultTableModel

class StatsTableModel : DefaultTableModel() {

    init {
        setColumnIdentifiers(arrayOf("Stat", "Value"))
    }

    fun addStat(stat: String, value: String) {
        (0 until rowCount).firstOrNull { getValueAt(it, 0) == stat }
            ?.let { setValueAt(value, it, 1) }
            ?: addRow(arrayOf(stat, value))
    }

    fun clearStats() {
        while (rowCount != 0) {
            removeRow(0)
        }
    }
}