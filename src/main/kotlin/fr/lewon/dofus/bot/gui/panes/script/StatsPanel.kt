package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.panes.script.stats.StatsTable
import fr.lewon.dofus.bot.gui.panes.script.stats.StatsTableModel
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.FormatUtil
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.util.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.border.LineBorder

class StatsPanel : JPanel(MigLayout()) {

    private var updateUITimer: Timer? = null
    private var scriptStartMillis = -1L
    private val scriptTimerPanel = JPanel(MigLayout()).also { it.border = LineBorder(Color.BLACK, 1) }
    private val timerLabel = JLabel()
    private val statsTable = StatsTable()
    private val statsScrollPane = JScrollPane(statsTable).also { it.horizontalScrollBar = null }
    private val statsPane = JPanel(MigLayout()).also { it.isVisible = false }

    init {
        scriptTimerPanel.add(JLabel("Time : "))
        scriptTimerPanel.add(timerLabel)
        add(scriptTimerPanel, "span 2 1, height 25, width max, wrap")
        add(JSeparator(JSeparator.HORIZONTAL), "span 3 1, width max, wrap")

        statsPane.add(JLabel("Stats").also { it.font = AppFonts.TITLE_FONT }, "wrap")
        statsPane.add(statsScrollPane, "width max, wrap")
        statsPane.minimumSize = Dimension(statsPane.minimumSize.width, 200)

        add(statsPane, "span 3 1, width max, height 100:max:max")
        updateTimerLabel()
    }

    private fun updateTimerLabel() {
        val durationMillis = if (scriptStartMillis > 0) System.currentTimeMillis() - scriptStartMillis else 0
        timerLabel.text = FormatUtil.durationToStr(durationMillis)
    }

    private fun updateScriptStats(script: DofusBotScript) {
        val model = statsTable.model as StatsTableModel
        model.clearStats()
        val stats = script.getStats()
        statsPane.isVisible = stats.isNotEmpty()
        stats.forEach { model.addStat(it.key, it.value) }
    }

    fun startStatsUpdate(script: DofusBotScript) {
        scriptStartMillis = System.currentTimeMillis()
        updateExecUI(script)
        updateUITimer = Timer()
        updateUITimer?.schedule(buildUpdateExecUITimerTask(script), 0, 1000)
    }

    private fun buildUpdateExecUITimerTask(script: DofusBotScript): TimerTask {
        return object : TimerTask() {
            override fun run() {
                updateExecUI(script)
            }
        }
    }

    private fun updateExecUI(script: DofusBotScript) {
        updateTimerLabel()
        updateScriptStats(script)
    }

    fun stopStatsUpdate() {
        updateUITimer?.cancel()
        updateUITimer = null
        scriptStartMillis = -1
    }

}