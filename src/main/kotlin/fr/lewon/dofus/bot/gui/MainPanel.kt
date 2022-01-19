package fr.lewon.dofus.bot.gui

import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui.about.AboutPanel
import fr.lewon.dofus.bot.gui.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.GlobalCharacterFormPanel
import fr.lewon.dofus.bot.gui.panes.config.ConfigPanel
import fr.lewon.dofus.bot.gui.panes.script.GlobalScriptPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.filemanagers.DofusClassManager
import fr.lewon.dofus.bot.util.filemanagers.HintManager
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

        mainTabbedPane.addChangeListener {
            val selectedComponent = mainTabbedPane.selectedComponent
            if (selectedComponent is GlobalScriptPanel) {
                CharacterSelectionPanel.cardList.getCard(selectedComponent.character)?.let {
                    CharacterSelectionPanel.cardList.selectItem(it)
                }
            } else {
                CharacterSelectionPanel.cardList.selectItem(null)
            }
        }

        CharacterSelectionPanel.border = BorderFactory.createEtchedBorder()
        leftTabbedPane.border = BorderFactory.createEtchedBorder()
        mainTabbedPane.border = BorderFactory.createEtchedBorder()
    }

    private fun addTab(title: String, buildIfMissingFunction: () -> JComponent): JComponent {
        var tab = tabByTitle[title]
        if (tab == null) {
            tab = buildIfMissingFunction()
            tabByTitle[title] = tab
            mainTabbedPane.addTab(title, tab)
        }
        mainTabbedPane.selectedComponent = tab
        return tab
    }

    fun addCharacterScriptTab(character: DofusCharacter): JComponent {
        return addTab("${character.pseudo} - Scripts") { GlobalScriptPanel(character) }
    }

    fun addCharacterEditTab(character: DofusCharacter?, onSaveAction: (DofusCharacter) -> Unit) {
        var tab: JComponent? = null
        val onSaveActionWithClose: (DofusCharacter) -> Unit = {
            onSaveAction(it)
            val index = mainTabbedPane.indexOfComponent(tab)
            val title = mainTabbedPane.getTitleAt(index)
            mainTabbedPane.remove(index)
            tabByTitle.remove(title)
        }
        tab = if (character == null) {
            addTab("New character") { GlobalCharacterFormPanel(DofusCharacter(), onSaveActionWithClose) }
        } else {
            addTab("${character.pseudo} - Edit") { GlobalCharacterFormPanel(character, onSaveActionWithClose) }
        }
    }

}

fun main() {
    VldbCoreInitializer.initAll()
    CharacterManager.initManager()
    DofusClassManager.initManager()
    ConfigManager.initManager()
    HintManager.initManager()
    VldbMainFrame.isVisible = true
}