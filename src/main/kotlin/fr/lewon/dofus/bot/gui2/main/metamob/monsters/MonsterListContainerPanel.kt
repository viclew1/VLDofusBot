package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import fr.lewon.dofus.bot.gui2.main.metamob.util.MetamobRequestProcessor
import fr.lewon.dofus.bot.gui2.util.AppFonts
import fr.lewon.dofus.bot.gui2.util.ImageUtil
import fr.lewon.dofus.bot.gui2.util.UiResource
import javax.swing.*

object MonsterListContainerPanel : JPanel() {

    private val monstersLabel = JLabel("Monsters").also {
        it.font = AppFonts.TITLE_FONT
    }
    private val monstersScrollPane = JScrollPane()
    private val refreshButton = JButton()
    private val errorLabel =
        JLabel("Error accessing server. Either Metamob is down or your have a problem in your config.")

    init {
        refreshButton.icon = ImageIcon(ImageUtil.getScaledImage(UiResource.REFRESH.imageData, 30, 30))
        refreshButton.rolloverIcon = ImageIcon(ImageUtil.getScaledImage(UiResource.REFRESH.filledImageData, 30, 30))
        refreshButton.isContentAreaFilled = false
        refreshButton.toolTipText = "Refresh"
        add(monstersLabel)
        add(refreshButton, "al left, wrap")
        add(errorLabel, "span 2 1, wrap")
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