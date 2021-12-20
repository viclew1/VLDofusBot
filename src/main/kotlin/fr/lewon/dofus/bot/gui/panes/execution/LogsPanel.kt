package fr.lewon.dofus.bot.gui.panes.execution

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import javax.swing.BorderFactory
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities

class LogsPanel(logger: VldbLogger) : JScrollPane(), VldbLoggerListener {

    private val textArea = JTextArea()

    init {
        textArea.lineWrap = true
        textArea.isEditable = false
        horizontalScrollBar = null
        border = BorderFactory.createEmptyBorder()
        setViewportView(textArea)
        logger.listeners.add(this)
    }

    override fun onLogsChange(logs: List<LogItem>) {
        SwingUtilities.invokeLater {
            textArea.text = logs.joinToString("\n")
            verticalScrollBar.value = verticalScrollBar.maximum
        }
    }
}