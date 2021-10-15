package fr.lewon.dofus.bot.gui.tabs.exec

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import fr.lewon.dofus.bot.gui.tabs.exec.stats.StatsTable
import fr.lewon.dofus.bot.gui.tabs.exec.stats.StatsTableModel
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.FormatUtil
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.util.*
import java.util.Timer
import javax.swing.*
import javax.swing.border.LineBorder

object ExecutionTab : JPanel(MigLayout()), ScriptRunnerListener, VldbLoggerListener {

    private lateinit var updateUITimer: Timer
    private var scriptStartMillis = -1L
    private val scriptLabel = JLabel("[None]").also { it.font = AppFonts.TITLE_FONT }
    private val scriptTimerPanel = JPanel(MigLayout()).also { it.border = LineBorder(Color.BLACK, 1) }
    private val timerLabel = JLabel()
    private val stopButton = JButton().also {
        it.icon = ImageIcon(ImageUtil.getScaledImage(UiResource.STOP.url, 25, 25))
        it.isBorderPainted = false
        it.border = null
        it.isContentAreaFilled = false
        it.rolloverIcon = ImageIcon(ImageUtil.getScaledImage(UiResource.STOP.filledUrl, 25, 25))
        it.isEnabled = false
        it.addActionListener { ScriptRunner.stopScript() }
    }
    private val statsTable = StatsTable()
    private val statsScrollPane = JScrollPane(statsTable).also { it.horizontalScrollBar = null }
    private val logsTextArea = JTextArea().also {
        it.lineWrap = true
        it.isEditable = false
    }
    private val executionLogsScrollPane = JScrollPane(logsTextArea).also { it.horizontalScrollBar = null }
    private val splitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT).also { it.dividerSize = 0 }
    private val splitPaneTop = JPanel(MigLayout()).also { it.isVisible = false }
    private val splitPaneBottom = JPanel(MigLayout())

    init {
        scriptTimerPanel.add(JLabel("Time : "))
        scriptTimerPanel.add(timerLabel)
        add(JLabel("Running script : "))
        add(scriptLabel, "wrap")
        add(scriptTimerPanel, "span 2 1, height 25, width max")
        add(stopButton, "wrap")
        add(JSeparator(JSeparator.HORIZONTAL), "span 3 1, width max, wrap")

        splitPaneTop.add(JLabel("Stats").also { it.font = AppFonts.TITLE_FONT }, "wrap")
        splitPaneTop.add(statsScrollPane, "width max, height max, wrap")
        splitPaneTop.minimumSize = Dimension(splitPaneTop.minimumSize.width, 200)
        splitPane.add(splitPaneTop)

        splitPaneBottom.add(JLabel("Execution logs").also { it.font = AppFonts.TITLE_FONT }, "wrap")
        splitPaneBottom.add(executionLogsScrollPane, "width max, height max")
        splitPane.add(splitPaneBottom)

        add(splitPane, "span 3 1, width max, height max")
        updateTimerLabel()
        ScriptRunner.listeners.add(this)
        VldbLogger.listeners.add(this)
    }

    private fun updateTimerLabel() {
        val durationMillis = if (scriptStartMillis > 0) System.currentTimeMillis() - scriptStartMillis else 0
        timerLabel.text = FormatUtil.durationToStr(durationMillis)
    }

    private fun updateScriptStats(script: DofusBotScript) {
        val model = statsTable.model as StatsTableModel
        model.clearStats()
        val stats = script.getStats()
        splitPane.dividerSize = if (stats.isEmpty()) 0 else 5
        splitPaneTop.isVisible = stats.isNotEmpty()
        stats.forEach { model.addStat(it.key, it.value) }
    }

    override fun onScriptStart(script: DofusBotScript) {
        scriptLabel.text = "[${script.name}]"
        stopButton.isEnabled = true
        scriptStartMillis = System.currentTimeMillis()
        updateExecUI(script)
        updateUITimer = Timer()
        updateUITimer.schedule(buildUpdateExecUITimerTask(script), 0, 1000)
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

    override fun onScriptEnd(endType: DofusBotScriptEndType) {
        stopButton.isEnabled = false
        updateUITimer.cancel()
        scriptStartMillis = -1
    }

    override fun onLogsChange(logs: List<LogItem>) {
        logsTextArea.text = logs.joinToString("\n\n")
        SwingUtilities.invokeLater {
            executionLogsScrollPane.verticalScrollBar.value = executionLogsScrollPane.verticalScrollBar.maximum
        }
    }
}