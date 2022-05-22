package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.panes.script.stats.StatsTable
import fr.lewon.dofus.bot.gui.panes.script.stats.StatsTableModel
import fr.lewon.dofus.bot.scripts.DofusBotScript
import net.miginfocom.swing.MigLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class StatsPanel : JPanel(MigLayout()) {

    private val statsTable = StatsTable()
    private val statsScrollPane = JScrollPane(statsTable).also { it.horizontalScrollBar = null }

    init {
        add(statsScrollPane, "width max, height 100:max:max")
    }

    fun updateScriptStats(script: DofusBotScript) {
        val model = statsTable.model as StatsTableModel
        model.clearStats()
        val stats = script.getStats()
        stats.forEach { model.addStat(it.key, it.value) }
    }

}