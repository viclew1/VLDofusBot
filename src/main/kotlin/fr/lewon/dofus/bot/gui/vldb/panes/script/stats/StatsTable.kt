package fr.lewon.dofus.bot.gui.vldb.panes.script.stats

import javax.swing.JTable

class StatsTable : JTable() {

    init {
        model = StatsTableModel()
        autoResizeMode = AUTO_RESIZE_ALL_COLUMNS
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return false
    }

}