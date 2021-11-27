package fr.lewon.dofus.bot.gui.init

import com.formdev.flatlaf.FlatDarkLaf
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.gui.custom.CustomFrame
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.util.io.WaitUtil
import fr.lewon.dofus.bot.util.listeners.KeyboardListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

object InitFrame : CustomFrame("VL Dofus Bot Initializer", 300, 500, AppColors.DEFAULT_UI_COLOR, 45) {

    init {
        InitPanel.setBounds(0, headerHeight, size.width, size.height - headerHeight)
        contentPane.add(InitPanel)
        iconImage = ImageIO.read(VLDofusBot::class.java.getResourceAsStream("/icon/taskbar_logo.png"))
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                exitProcess(0)
            }
        })
        SwingUtilities.invokeLater {
            FlatDarkLaf.setUseNativeWindowDecorations(false)
            FlatDarkLaf.setup()
            UIManager.setLookAndFeel(FlatDarkLaf())
            SwingUtilities.updateComponentTreeUI(this)
        }
    }

    fun startInit() {
        if (InitPanel.initAll()) {
            KeyboardListener.start()
            WaitUtil.sleep(1000)
            MainFrame.isResizable = false
            MainFrame.isUndecorated = true
            MainFrame.setLocationRelativeTo(this)
            MainFrame.isVisible = true
            defaultCloseOperation = DO_NOTHING_ON_CLOSE
            windowListeners.forEach { removeWindowListener(it) }
            dispose()
        }
    }

}