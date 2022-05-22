package fr.lewon.dofus.bot.gui

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.gui.custom.CustomHeaderButton
import fr.lewon.dofus.bot.gui.custom.WindowDragListener
import fr.lewon.dofus.bot.gui.custom.ext.ComponentResizer
import fr.lewon.dofus.bot.gui.panes.status.StatusPanel
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.LineBorder
import kotlin.system.exitProcess

object VldbMainFrame : JFrame("VL Dofus Bot") {

    private const val MIN_WIDTH = 1024
    private const val MIN_HEIGHT = 680
    private const val DEFAULT_WIDTH = 1024
    private const val DEFAULT_HEIGHT = 768
    private const val HEADER_HEIGHT = 60
    private const val HEADER_BUTTON_WIDTH = 30
    const val FOOTER_HEIGHT = 30

    init {
        ToolTipManager.sharedInstance().initialDelay = 0
        ToolTipManager.sharedInstance().dismissDelay = 10000
        isUndecorated = true
        defaultCloseOperation = EXIT_ON_CLOSE
        iconImage = ImageIO.read(VLDofusBot::class.java.getResourceAsStream("/icon/taskbar_logo.png"))
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent) {
                exitProcess(0)
            }
        })
        SwingUtilities.invokeLater {
            FlatLaf.setUseNativeWindowDecorations(false)
            FlatLaf.setup(FlatDarkLaf())
            SwingUtilities.updateComponentTreeUI(this)
        }
        rootPane.border = LineBorder(AppColors.DEFAULT_UI_COLOR, 2)
        contentPane.layout = MigLayout("insets 0, gapY 0")
        initSize()
        buildHeader()
        buildMainPane()
        buildFooter()
    }

    private fun initSize() {
        val cr = ComponentResizer()
        cr.registerComponent(this)
        cr.snapSize = Dimension(10, 10)
        cr.minimumSize = Dimension(MIN_WIDTH, MIN_HEIGHT)
        size = Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    }

    private fun buildHeader() {
        val lblBd = JLabel(title)
        lblBd.isOpaque = true
        lblBd.background = AppColors.DEFAULT_UI_COLOR
        lblBd.foreground = Color.WHITE
        lblBd.font = Font("Times New Roman", Font.BOLD, 20)
        lblBd.icon = ImageIcon(ImageUtil.getScaledImageKeepHeight(UiResource.VLDB_LOGO.imageData, HEADER_HEIGHT))

        val header = JPanel(MigLayout("insets 0"))
        header.add(lblBd, "al left, pushX")
        header.add(CustomHeaderButton("-") { state = ICONIFIED }, "w $HEADER_BUTTON_WIDTH, al right")
        header.add(CustomHeaderButton("x") { dispose() }, "w $HEADER_BUTTON_WIDTH, al right")
        header.background = AppColors.DEFAULT_UI_COLOR
        contentPane.add(header, "w max, wrap")

        val dragListener = WindowDragListener(this)
        header.addMouseListener(dragListener)
        header.addMouseMotionListener(dragListener)
    }

    private fun buildMainPane() {
        contentPane.add(MainPanel, "w max, pushY, growY, wrap")
    }

    private fun buildFooter() {
        contentPane.add(StatusPanel, "w max, h $FOOTER_HEIGHT:$FOOTER_HEIGHT:$FOOTER_HEIGHT")
    }

}