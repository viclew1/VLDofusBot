package fr.lewon.dofus.bot.gui.panes.config

import fr.lewon.dofus.bot.core.logs.LogLevel
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import net.miginfocom.swing.MigLayout
import javax.swing.*

object ConfigPanel : JPanel(MigLayout()) {

    private val logLevelLabel = JLabel("Log Level")
    private val logLevelComboBox = JComboBox(LogLevel.values())
    private val locateCursorLabel = JLabel("Locate cursor")
    private val locateCursorButton = JButton("Locate")

    init {
        val logLevel = LogLevel.valueOf(ConfigManager.config.logLevel)
        updateLogLevel(logLevel)
        logLevelComboBox.selectedItem = logLevel
        addLine(logLevelLabel, logLevelComboBox)
        logLevelComboBox.addItemListener { updateLogLevel(logLevelComboBox.selectedItem as LogLevel) }

        locateCursorButton.addActionListener { locatePoint() }

        addLine(locateCursorLabel, locateCursorButton)
    }

    private fun locatePoint() {
        val locatorFrame = CursorLocatorFrame("Click on position to register") {
            println("-----")
            val currentCharacter = CharacterManager.getCurrentCharacter()
            currentCharacter?.let { c ->
                GameSnifferUtil.getConnection(c)?.let { connection ->
                    val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
                    JNAUtil.updateGameBounds(gameInfo)
                    val windowPos = JNAUtil.getGamePosition(connection.pid)
                    val pointAbsolute = PointAbsolute(it.x - windowPos.x, it.y - windowPos.y)
                    println("PointAbsolute(${pointAbsolute.x}, ${pointAbsolute.y})")
                    val pointRelative = ConverterUtil.toPointRelative(gameInfo, pointAbsolute)
                    println("PointRelative(${pointRelative.x}f, ${pointRelative.y}f)")
                    val uiPoint = ConverterUtil.toUIPoint(gameInfo, pointAbsolute)
                    println("UIPoint(${uiPoint.x}, ${uiPoint.y})")
                }
            } ?: println("Couldn't find dofus connection information.")
        }
        locatorFrame.isVisible = true
    }

    private fun updateLogLevel(logLevel: LogLevel) {
        ConfigManager.editConfig { it.logLevel = logLevel.name }
    }

    private fun addLine(leftComponent: JComponent, rightComponent: JComponent, separator: Boolean = true) {
        add(leftComponent)
        add(rightComponent, "width 80, al right, wrap")
        if (separator) addSeparator()
    }

    private fun addSeparator() {
        add(JSeparator(JSeparator.HORIZONTAL), "span 2 1, width max, wrap")
    }

}