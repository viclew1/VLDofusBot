package fr.lewon.dofus.bot.gui

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLightLaf
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.gui.custom.CustomFrame
import fr.lewon.dofus.bot.gui.util.AppColors
import java.awt.Color
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JProgressBar
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

object MainFrame :
    CustomFrame(
        "VL Dofus Bot",
        300,
        700,
        AppColors.DEFAULT_UI_COLOR,
        60,
        MainFrame::class.java.getResource("/icon/logo.png")
    ) {

    private const val progressBarHeight = 10
    private val progressBar = JProgressBar()

    init {
        buildFooter()
        MainPanel.setBounds(0, headerHeight, size.width, size.height - headerHeight - progressBarHeight)
        contentPane.add(MainPanel)
        isUndecorated = true
        isAlwaysOnTop = true
        defaultCloseOperation = EXIT_ON_CLOSE
        iconImage =
            Toolkit.getDefaultToolkit()
                .getImage(VLDofusBot::class.java.getResource("/icon/taskbar_logo.png"))
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                exitProcess(0)
            }
        })
    }

    private fun buildFooter() {
        progressBar.setBounds(0, size.height - progressBarHeight, size.width, progressBarHeight)
        progressBar.isBorderPainted = false
        progressBar.background = background
        contentPane.add(progressBar)
    }

    fun loading() {
        progressBar.background = background
        progressBar.isBorderPainted = false
        progressBar.isIndeterminate = true
    }

    fun stopLoading(resultColor: Color) {
        progressBar.isIndeterminate = false
        progressBar.isBorderPainted = true
        progressBar.background = resultColor
    }

    fun updateAlwaysOnTop(alwaysOnTop: Boolean) {
        SwingUtilities.invokeLater {
            isAlwaysOnTop = alwaysOnTop
        }
    }

    fun lightMode() {
        SwingUtilities.invokeLater {
            FlatLightLaf.setUseNativeWindowDecorations(false)
            FlatLightLaf.setup()
            UIManager.setLookAndFeel(FlatLightLaf())
            SwingUtilities.updateComponentTreeUI(this)
        }
    }

    fun darkMode() {
        SwingUtilities.invokeLater {
            FlatDarkLaf.setUseNativeWindowDecorations(false)
            FlatDarkLaf.setup()
            UIManager.setLookAndFeel(FlatDarkLaf())
            SwingUtilities.updateComponentTreeUI(this)
        }
    }

}