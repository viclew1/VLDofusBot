package fr.lewon.dofus.bot.gui.metamobhelper.monsters

import fr.lewon.dofus.bot.gui.metamobhelper.util.MetamobRequestProcessor
import fr.lewon.dofus.bot.gui.util.AppFonts
import net.miginfocom.swing.MigLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

object MonsterListContainerPanel : JPanel(MigLayout()) {

    private val monstersLabel = JLabel("Monsters").also { it.font = AppFonts.TITLE_FONT }
    private val monstersScrollPane = JScrollPane()
    private val refreshButton = JButton("Refresh")
    private val errorLabel =
        JLabel("Error accessing server. Either Metamob is down or your have a problem in your config.")

    init {
        add(monstersLabel, "wrap")
        add(refreshButton)
        add(errorLabel, "wrap")
        errorLabel.isVisible = false
        refreshButton.addActionListener { refresh() }
        monstersScrollPane.horizontalScrollBar = null
        add(monstersScrollPane, "span 2 1, h max, width max, wrap")
        monstersScrollPane.setViewportView(MonsterListPanel)
        monstersScrollPane.verticalScrollBar.unitIncrement *= 5
        refresh()
    }

    fun refresh() {
        val monsters = MetamobRequestProcessor.getAllMonsters()
        if (monsters == null) {
            MonsterListPanel.updateMonsters(emptyList())
            errorLabel.isVisible = true
        } else {
            MonsterListPanel.updateMonsters(monsters)
            errorLabel.isVisible = false
        }
    }

}