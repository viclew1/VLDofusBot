package fr.lewon.dofus.bot.gui.tabs.config

import fr.lewon.dofus.bot.core.logs.LogLevel
import fr.lewon.dofus.bot.core.logs.VldbLogger
import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.io.ConverterUtil
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import net.miginfocom.swing.MigLayout
import javax.swing.*

object ConfigTab : JPanel(MigLayout()) {

    private val darkModeLabel = JLabel("Dark mode")
    private val darkModeCheckBox = JCheckBox()
    private val alwaysOnTopLabel = JLabel("Always on top")
    private val alwaysOnTopCheckBox = JCheckBox()
    private val logLevelLabel = JLabel("Log Debug")
    private val logLevelComboBox = JComboBox(LogLevel.values())
    private val locateCursorLabel = JLabel("Locate cursor")
    private val locateCursorButton = JButton("Locate")

    init {
        updateDarkMode(ConfigManager.config.darkMode)
        addLine(darkModeLabel, darkModeCheckBox)
        darkModeCheckBox.addItemListener { updateDarkMode(darkModeCheckBox.isSelected) }

        updateAlwaysOnTop(ConfigManager.config.alwaysOnTop)
        addLine(alwaysOnTopLabel, alwaysOnTopCheckBox)
        alwaysOnTopCheckBox.addItemListener { updateAlwaysOnTop(alwaysOnTopCheckBox.isSelected) }

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
                GameSnifferUtil.getCharacterPID(c)?.let { pid ->
                    val gameInfo = GameSnifferUtil.getGameInfoByPID(pid)
                    JNAUtil.updateGameBounds(gameInfo, pid)
                    val windowPos = JNAUtil.getGamePosition(pid)
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

    private fun updateDarkMode(darkMode: Boolean) {
        ConfigManager.editConfig { it.darkMode = darkMode }
        darkModeCheckBox.isSelected = darkMode
        if (darkMode) {
            MainFrame.darkMode()
        } else {
            MainFrame.lightMode()
        }
    }

    private fun updateAlwaysOnTop(alwaysOnTop: Boolean) {
        ConfigManager.editConfig { it.alwaysOnTop = alwaysOnTop }
        alwaysOnTopCheckBox.isSelected = alwaysOnTop
        MainFrame.updateAlwaysOnTop(alwaysOnTop)
    }

    private fun updateLogLevel(logLevel: LogLevel) {
        ConfigManager.editConfig { it.logLevel = logLevel.name }
        VldbLogger.minLogLevel = logLevel
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