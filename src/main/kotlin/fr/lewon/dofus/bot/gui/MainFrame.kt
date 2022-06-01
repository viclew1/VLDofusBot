package fr.lewon.dofus.bot.gui

import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import fr.lewon.dofus.bot.AbstractMainPanel
import fr.lewon.dofus.bot.VLDofusBot
import fr.lewon.dofus.bot.gui.custom.CustomHeaderButton
import fr.lewon.dofus.bot.gui.custom.WindowDragListener
import fr.lewon.dofus.bot.gui.custom.ext.ComponentResizer
import fr.lewon.dofus.bot.gui.metamobhelper.MetamobHelperMainPanel
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.gui.vldb.VldbMainPanel
import fr.lewon.dofus.bot.gui.vldb.panes.status.StatusPanel
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.LineBorder
import kotlin.system.exitProcess

object MainFrame : JFrame("VL Dofus Bot") {

    private const val MIN_WIDTH = 1024
    private const val MIN_HEIGHT = 680
    private const val DEFAULT_WIDTH = 1024
    private const val DEFAULT_HEIGHT = 768
    private const val HEADER_HEIGHT = 60
    private const val HEADER_BUTTON_WIDTH = 30
    const val FOOTER_HEIGHT = 30

    private val lblApp = JLabel()
    private val contentSelector = ButtonWithMenuSubButton()
    private val contentSelectorPopupMenu = JPopupMenu()
    private val mainContentPane = JPanel(MigLayout("gapX 0, gapY 0, fill, insets 0"))

    init {
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
        ToolTipManager.sharedInstance().initialDelay = 0
        ToolTipManager.sharedInstance().dismissDelay = 10000
        UIManager.put("MenuItem.selectionBackground", AppColors.DEFAULT_UI_COLOR)
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
        lblApp.isOpaque = true
        lblApp.background = AppColors.DEFAULT_UI_COLOR
        lblApp.foreground = Color.WHITE
        lblApp.font = Font("Times New Roman", Font.BOLD, 20)

        buildContentSelector()
        val header = JPanel(MigLayout("insets 0"))
        header.add(contentSelector, "al left, w 70:70:70")
        header.add(lblApp, "al left, pushX")
        header.add(CustomHeaderButton("-") { state = ICONIFIED }, "w $HEADER_BUTTON_WIDTH, al right")
        header.add(CustomHeaderButton("x") { dispose() }, "w $HEADER_BUTTON_WIDTH, al right")
        header.background = AppColors.DEFAULT_UI_COLOR
        contentPane.add(header, "w max, wrap")

        val dragListener = WindowDragListener(this)
        header.addMouseListener(dragListener)
        header.addMouseMotionListener(dragListener)

        changeContent(AppContent.SCRIPTS)
    }

    private fun buildContentSelector() {
        contentSelector.isContentAreaFilled = false
        AppContent.values().forEach {
            contentSelectorPopupMenu.add(buildContentSelectorItem(it))
        }
        contentSelector.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                contentSelectorPopupMenu.show(contentSelector, 0, contentSelector.height)
            }
        })
    }

    private fun buildContentSelectorItem(appContent: AppContent): JMenuItem {
        val icon = ImageIcon(ImageUtil.getScaledImageKeepHeight(appContent.uiResource.imageData, HEADER_HEIGHT * 2 / 3))
        val menuItem = JMenuItem(appContent.title, icon)
        menuItem.addActionListener { changeContent(appContent) }
        menuItem.font = AppFonts.TITLE_FONT
        menuItem.iconTextGap = 60 - menuItem.icon.iconWidth
        return menuItem
    }

    private fun changeContent(appContent: AppContent) {
        lblApp.text = "$title - ${appContent.title}"
        contentSelector.icon =
            ImageIcon(ImageUtil.getScaledImageKeepHeight(appContent.uiResource.imageData, HEADER_HEIGHT))
        mainContentPane.removeAll()
        mainContentPane.add(appContent.content, "w max, pushY, growY, wrap")
        appContent.content.updateLeftBottomPane()
        super.getRootPane().updateUI()
    }

    private fun buildMainPane() {
        contentPane.add(mainContentPane, "w max, pushY, growY, wrap")
    }

    private fun buildFooter() {
        contentPane.add(StatusPanel, "w max, h $FOOTER_HEIGHT:$FOOTER_HEIGHT:$FOOTER_HEIGHT")
    }

    private enum class AppContent(val title: String, val uiResource: UiResource, val content: AbstractMainPanel) {
        SCRIPTS("Scripts", UiResource.VLDB_LOGO, VldbMainPanel),
        METAMOB_HELPER("Metamob Helper", UiResource.METAMOB_HELPER_LOGO, MetamobHelperMainPanel),
    }

    private class ButtonWithMenuSubButton : JButton() {

        private val changeIcon = ImageIcon(ImageUtil.getScaledImage(UiResource.MENU.imageData, 30))
        private val changeIconFilled = ImageIcon(ImageUtil.getScaledImage(UiResource.MENU.filledImageData, 30))

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            g.color = Color.WHITE
            val toDrawIcon = if (this.model.isRollover) changeIconFilled else changeIcon
            toDrawIcon.paintIcon(null, g, -8, height - toDrawIcon.iconHeight)
        }
    }

}