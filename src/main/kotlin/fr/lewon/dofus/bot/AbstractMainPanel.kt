package fr.lewon.dofus.bot

import fr.lewon.dofus.bot.gui.vldb.about.AboutPanel
import fr.lewon.dofus.bot.gui.vldb.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.gui.vldb.panes.config.ConfigPanel
import fr.lewon.dofus.bot.gui.vldb.panes.config.MetamobConfigPanel
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTabbedPane

abstract class AbstractMainPanel : JPanel(MigLayout("gapX 0, gapY 0, fill, insets 0")) {

    companion object {
        const val LEFT_PANE_WIDTH = 260
        private const val CONFIG_TABS_HEIGHT = 210
    }

    private val leftTabbedPane = JTabbedPane()

    init {
        val leftPanel = JPanel(MigLayout("Insets 0, gapX 0, gapY 0"))
        leftPanel.add(getLeftPaneTopContent(), "h max, wrap")
        leftPanel.add(leftTabbedPane, "h $CONFIG_TABS_HEIGHT:$CONFIG_TABS_HEIGHT:$CONFIG_TABS_HEIGHT")
        add(leftPanel, "w $LEFT_PANE_WIDTH:$LEFT_PANE_WIDTH:$LEFT_PANE_WIDTH, h max")
        val rightPane = getRightPaneContent()
        add(rightPane, "w max, h max")


        CharacterSelectionPanel.border = BorderFactory.createEtchedBorder()
        leftTabbedPane.border = BorderFactory.createEtchedBorder()
        rightPane.border = BorderFactory.createEtchedBorder()
    }

    fun updateLeftBottomPane() {
        leftTabbedPane.removeAll()
        leftTabbedPane.addTab("Global Conf.", ConfigPanel)
        leftTabbedPane.addTab("Metamob Conf.", MetamobConfigPanel)
        leftTabbedPane.addTab("About", AboutPanel)
    }

    protected abstract fun getLeftPaneTopContent(): JComponent

    protected abstract fun getRightPaneContent(): JComponent

}