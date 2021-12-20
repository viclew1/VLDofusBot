package fr.lewon.dofus.bot.gui

import fr.lewon.dofus.bot.core.VLDofusBotCoreUtil
import fr.lewon.dofus.bot.gui.about.AboutPanel
import fr.lewon.dofus.bot.gui.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.gui.panes.config.ConfigPanel
import fr.lewon.dofus.bot.gui.panes.script.GlobalScriptPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTabbedPane

object MainPanel : JPanel(MigLayout("gapX 0, gapY 0, fill, insets 0")) {

    const val CHARACTERS_WIDTH = 260
    private const val CONFIG_TABS_HEIGHT = 200

    private val leftTabbedPane = JTabbedPane()
    private val mainTabbedPane = JTabbedPane()
    private val tabByTitle = HashMap<String, JComponent>()

    init {
        val leftPanel = JPanel(MigLayout("Insets 0, gapX 0, gapY 0"))
        leftTabbedPane.addTab("Config", ConfigPanel)
        leftTabbedPane.addTab("About", AboutPanel)
        leftPanel.add(CharacterSelectionPanel, "h max, wrap")
        leftPanel.add(leftTabbedPane, "h $CONFIG_TABS_HEIGHT:$CONFIG_TABS_HEIGHT:$CONFIG_TABS_HEIGHT")
        add(leftPanel, "w $CHARACTERS_WIDTH:$CHARACTERS_WIDTH:$CHARACTERS_WIDTH, h max")
        add(mainTabbedPane, "w max, h max")

        CharacterSelectionPanel.border = BorderFactory.createEtchedBorder()
        leftTabbedPane.border = BorderFactory.createEtchedBorder()
        mainTabbedPane.border = BorderFactory.createEtchedBorder()

        CharacterManager.getCurrentCharacter()?.let { addCharacterScriptTab(it) }
    }

    private fun addTab(title: String, buildIfMissingFunction: () -> JComponent) {
        var tab = tabByTitle[title]
        if (tab == null) {
            tab = buildIfMissingFunction()
            tabByTitle[title] = tab
            mainTabbedPane.addTab(title, tab)
        }
        mainTabbedPane.selectedComponent = tab
    }

    fun addCharacterScriptTab(character: DofusCharacter) {
        addTab("${character.pseudo} - Scripts") { GlobalScriptPanel(character) }
    }

}

fun main() {
    VLDofusBotCoreUtil.initAll()
    VLDofusBotCoreUtil.initVldbManagers(CharacterManager::class.java.packageName)
    VldbMainFrame.isVisible = true
}