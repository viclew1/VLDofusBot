package fr.lewon.dofus.bot.gui

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatLightLaf
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.core.VLDofusBotCoreUtil
import fr.lewon.dofus.bot.gui.custom.CustomFrame
import fr.lewon.dofus.bot.gui.tabs.exec.ExecutionTab
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JProgressBar
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

object MainFrame : CustomFrame(
    "VL Dofus Bot", 309, 700, AppColors.DEFAULT_UI_COLOR, 60,
    MainFrame::class.java.getResource("/icon/logo.png")
), ScriptRunnerListener {
    private const val progressBarHeight = 10
    private val progressBar = JProgressBar()

    init {
        buildFooter()
        MainPanel.setBounds(0, headerHeight, size.width, size.height - headerHeight - progressBarHeight)
        contentPane.add(MainPanel)
        isAlwaysOnTop = true
        defaultCloseOperation = EXIT_ON_CLOSE
        iconImage = Toolkit.getDefaultToolkit().getImage(VLDofusBot::class.java.getResource("/icon/taskbar_logo.png"))
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                exitProcess(0)
            }
        })
        ScriptRunner.listeners.add(this)
    }

    private fun buildFooter() {
        progressBar.setBounds(0, size.height - progressBarHeight, size.width, progressBarHeight)
        progressBar.isBorderPainted = false
        progressBar.background = background
        contentPane.add(progressBar)
    }

    fun updateAlwaysOnTop(alwaysOnTop: Boolean) {
        SwingUtilities.invokeLater {
            isAlwaysOnTop = alwaysOnTop
        }
    }

    fun lightMode() {
        updateLaf(FlatLightLaf())
    }

    fun darkMode() {
        updateLaf(FlatDarkLaf())
    }

    private fun updateLaf(laf: FlatLaf) {
        SwingUtilities.invokeLater {
            FlatLaf.setUseNativeWindowDecorations(false)
            FlatLaf.setup(laf)
            SwingUtilities.updateComponentTreeUI(this)
        }
    }

    override fun onScriptEnd(endType: DofusBotScriptEndType) {
        progressBar.isIndeterminate = false
        progressBar.isBorderPainted = true
        progressBar.background = getScriptEndColor(endType)
    }

    private fun getScriptEndColor(endType: DofusBotScriptEndType): Color {
        return when (endType) {
            DofusBotScriptEndType.FAIL -> Color.RED
            DofusBotScriptEndType.CANCEL -> Color.ORANGE
            DofusBotScriptEndType.SUCCESS -> Color.GREEN
        }
    }

    override fun onScriptStart(script: DofusBotScript) {
        MainPanel.selectedComponent = ExecutionTab
        progressBar.isBorderPainted = false
        progressBar.isIndeterminate = true
    }

}

fun main() {
    VLDofusBotCoreUtil.initAll()
    MainFrame.isResizable = false
    MainFrame.isUndecorated = true
    MainFrame.setLocationRelativeTo(null)
    MainFrame.isVisible = true
}