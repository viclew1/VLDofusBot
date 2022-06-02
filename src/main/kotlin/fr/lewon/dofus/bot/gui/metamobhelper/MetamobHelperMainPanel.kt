package fr.lewon.dofus.bot.gui.metamobhelper

import fr.lewon.dofus.bot.AbstractMainPanel
import fr.lewon.dofus.bot.gui.metamobhelper.filter.MonsterFilterGlobalPanel
import fr.lewon.dofus.bot.gui.metamobhelper.monsters.MonsterListContainerPanel
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTabbedPane

object MetamobHelperMainPanel : AbstractMainPanel() {

    override fun getLeftPaneTopContent(): JPanel {
        return MonsterFilterGlobalPanel.also { it.border = BorderFactory.createEtchedBorder() }
    }

    override fun getRightPaneContent(): JComponent {
        val tabbedPane = JTabbedPane()
        tabbedPane.addTab("Monsters", MonsterListContainerPanel)
        tabbedPane.addTab("Community", JPanel())
        tabbedPane.addTab("Help", JPanel())
        return tabbedPane
    }

}