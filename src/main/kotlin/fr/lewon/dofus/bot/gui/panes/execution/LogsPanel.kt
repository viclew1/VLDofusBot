package fr.lewon.dofus.bot.gui.panes.execution

import fr.lewon.dofus.bot.core.logs.LogItem
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.core.logs.VldbLoggerListener
import fr.lewon.dofus.bot.gui.custom.ext.CollapsiblePanel
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import net.miginfocom.swing.MigLayout
import java.awt.*
import javax.swing.*
import javax.swing.plaf.metal.MetalToggleButtonUI


class LogsPanel(
    private val logger: VldbLogger, canPauseLogsReception: Boolean = false
) : JPanel(MigLayout()), VldbLoggerListener {

    companion object {
        private const val BUTTON_SZ = 36
    }

    private val storedLogItemWithComponentById = HashMap<Long, Pair<LogItem, Component>>()
    private val holder = JPanel(BorderLayout())
    private val logItemsPanel = JPanel(GridBagLayout())
    private val logsScrollPane = JScrollPane(holder)

    private var autoScroll = true
    private var pauseLogs = false

    init {
        holder.add(logItemsPanel, BorderLayout.PAGE_START)
        val headerPanel = JPanel(MigLayout())
        val headerButtonConstraints = "w $BUTTON_SZ:$BUTTON_SZ:$BUTTON_SZ, h $BUTTON_SZ:$BUTTON_SZ:$BUTTON_SZ"

        val autoScrollButton = createHeaderToggleButton("Auto scroll down", UiResource.AUTO_SCROLL)
        autoScrollButton.isSelected = true
        autoScrollButton.addActionListener { autoScroll = autoScrollButton.isSelected }
        headerPanel.add(autoScrollButton, headerButtonConstraints)

        if (canPauseLogsReception) {
            val pauseLogsButton = createHeaderToggleButton("Pause log reception", UiResource.PAUSE)
            pauseLogsButton.isSelected = false
            pauseLogsButton.addActionListener { pauseLogs = pauseLogsButton.isSelected }
            headerPanel.add(pauseLogsButton, headerButtonConstraints)
        }

        val clearLogsButton = createHeaderButton("Clear logs", UiResource.ERASE)
        clearLogsButton.addActionListener { clearLogs() }
        headerPanel.add(clearLogsButton, headerButtonConstraints)

        add(headerPanel, "w max, wrap")

        logsScrollPane.verticalScrollBar.unitIncrement *= 5
        logsScrollPane.horizontalScrollBar = null
        logsScrollPane.border = BorderFactory.createEtchedBorder()
        logger.listeners.add(this)
        add(logsScrollPane, "w max, h max")
    }

    private fun createHeaderToggleButton(tooltipText: String, uiResource: UiResource): JToggleButton {
        val button = JToggleButton()
        button.toolTipText = tooltipText
        val imageData = uiResource.imageData
        val filledImageData = uiResource.filledImageData
        button.icon = ImageIcon(ImageUtil.getScaledImage(imageData, BUTTON_SZ, BUTTON_SZ))
        button.selectedIcon = ImageIcon(ImageUtil.getScaledImage(filledImageData, BUTTON_SZ, BUTTON_SZ))
        button.setUI(object : MetalToggleButtonUI() {
            override fun getSelectColor(): Color {
                return AppColors.DEFAULT_UI_COLOR
            }
        })
        return button
    }

    private fun createHeaderButton(tooltipText: String, uiResource: UiResource): JButton {
        val button = JButton()
        button.toolTipText = tooltipText
        val imageData = uiResource.imageData
        val filledImageData = uiResource.filledImageData
        button.icon = ImageIcon(ImageUtil.getScaledImage(imageData, BUTTON_SZ, BUTTON_SZ))
        button.rolloverIcon = ImageIcon(ImageUtil.getScaledImage(filledImageData, BUTTON_SZ, BUTTON_SZ))
        button.isContentAreaFilled = false
        return button
    }

    private fun clearLogs() {
        SwingUtilities.invokeLater {
            logger.clearLogs()
            storedLogItemWithComponentById.clear()
            logItemsPanel.removeAll()
            super.getRootPane().updateUI()
        }
    }

    override fun onLogsChange(logs: List<LogItem>) {
        if (!pauseLogs) {
            SwingUtilities.invokeLater {
                val oldScrollValue = logsScrollPane.verticalScrollBar.value
                for (logItem in logs) {
                    if (!storedLogItemWithComponentById.containsKey(logItem.id)) {
                        addLogItem(logItem)
                    }
                }
                storedLogItemWithComponentById.maxByOrNull { it.key }?.let {
                    updateLogItemComponent(it.value.second, it.value.first)
                }
                SwingUtilities.invokeLater {
                    logsScrollPane.verticalScrollBar.value = if (!autoScroll) {
                        oldScrollValue
                    } else {
                        logsScrollPane.verticalScrollBar.maximum
                    }
                }
            }
        }
    }

    private fun addLogItem(logItem: LogItem) {
        if (storedLogItemWithComponentById.size >= logger.logItemCapacity) {
            val toRemove = storedLogItemWithComponentById.minByOrNull { it.key }
                ?: return
            storedLogItemWithComponentById.remove(toRemove.key)
            logItemsPanel.remove(toRemove.value.second)
        }
        val logItemComponent = buildLogItemComponent(logItem)

        val gbc = GridBagConstraints()
        gbc.gridwidth = GridBagConstraints.REMAINDER
        gbc.anchor = GridBagConstraints.FIRST_LINE_START
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        logItemsPanel.add(logItemComponent, gbc)
        storedLogItemWithComponentById[logItem.id] = logItem to logItemComponent
    }

    private fun buildLogItemComponent(logItem: LogItem): Component {
        return if (logItem.description.isEmpty()) {
            buildLogItemCellWithoutDescription(logItem)
        } else {
            buildLogItemCellWithDescription(logItem)
        }
    }

    private fun updateLogItemComponent(component: Component, logItem: LogItem) {
        if (logItem.description.isEmpty()) {
            (component as JTextArea).text = logItem.toString()
        } else {
            val collapsiblePanel = (component as CollapsiblePanel)
            collapsiblePanel.setTitleComponentText(logItem.toString())
            collapsiblePanel.contentPane
        }
    }

    private fun buildLogItemCellWithoutDescription(logItem: LogItem): Component {
        return JTextArea(logItem.toString()).also {
            it.lineWrap = true
            it.isEditable = false
        }
    }

    private fun buildLogItemCellWithDescription(logItem: LogItem): Component {
        return CollapsiblePanel(logItem.toString()).also {
            val textArea = JTextArea(logItem.description)
            textArea.lineWrap = true
            textArea.isEditable = false
            it.contentPane.add(textArea)
            it.insets.set(0, 0, 0, 0)
        }
    }


}