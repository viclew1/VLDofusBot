package fr.lewon.dofus.bot.gui.vldb

import com.formdev.flatlaf.ui.FlatTabbedPaneUI
import fr.lewon.dofus.bot.AbstractMainPanel
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.gui.vldb.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.CharacterFormPanel
import fr.lewon.dofus.bot.gui.vldb.panes.script.CharacterScriptPanel
import fr.lewon.dofus.bot.gui.vldb.panes.script.GlobalScriptPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.*
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JComponent
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities

object VldbMainPanel : AbstractMainPanel() {

    const val CHARACTERS_WIDTH = LEFT_PANE_WIDTH

    private lateinit var mainTabbedPane: JTabbedPane
    private lateinit var tabByTitle: HashMap<String, JComponent>

    override fun getLeftPaneTopContent(): JComponent {
        return CharacterSelectionPanel
    }

    override fun getRightPaneContent(): JComponent {
        mainTabbedPane = JTabbedPane()
        tabByTitle = HashMap()
        mainTabbedPane.addChangeListener {
            val selectedComponent = mainTabbedPane.selectedComponent
            if (selectedComponent is CharacterScriptPanel) {
                CharacterSelectionPanel.cardList.getCard(selectedComponent.character)?.let {
                    CharacterSelectionPanel.cardList.selectItem(it)
                }
            } else {
                CharacterSelectionPanel.cardList.selectItem(null)
            }
        }
        SwingUtilities.invokeLater {
            mainTabbedPane.setUI(object : FlatTabbedPaneUI() {
                override fun createMouseListener(): MouseListener {
                    return buildHeaderListener()
                }
            })
            addTab("Global scripts") { GlobalScriptPanel }
        }
        return mainTabbedPane
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

    private fun buildHeaderListener(): MouseAdapter {
        return object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val index = mainTabbedPane.ui.tabForCoordinate(mainTabbedPane, e.x, e.y)
                if (index >= 0) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (mainTabbedPane.selectedIndex != index) {
                            mainTabbedPane.selectedIndex = index
                        } else if (mainTabbedPane.isRequestFocusEnabled) {
                            mainTabbedPane.requestFocusInWindow()
                        }
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        removeTab(mainTabbedPane.getComponentAt(index))
                    }
                }
            }
        }
    }

    private fun removeTab(tab: Component) {
        if (tab !is GlobalScriptPanel) {
            val index = mainTabbedPane.indexOfComponent(tab)
            val title = mainTabbedPane.getTitleAt(index)
            mainTabbedPane.remove(index)
            tabByTitle.remove(title)
            if (tab is ResourceConsumingPanel) {
                tab.stopAll()
            }
        }
    }

    fun addCharacterScriptTab(character: DofusCharacter): JComponent {
        return addTab("${character.pseudo} - Scripts") { CharacterScriptPanel(character) }
    }

    fun addCharacterEditTab(character: DofusCharacter?, onSaveAction: (DofusCharacter) -> Unit) {
        var tab: JComponent? = null
        val onSaveActionWithClose: (DofusCharacter) -> Unit = {
            onSaveAction(it)
            tab?.let { t -> removeTab(t) }
            addCharacterScriptTab(it)
        }
        tab = if (character == null) {
            addTab("New character") { CharacterFormPanel(DofusCharacter(), onSaveActionWithClose) }
        } else {
            addTab("${character.pseudo} - Edit") { CharacterFormPanel(character, onSaveActionWithClose) }
        }
    }

}

fun main() {
    VldbCoreInitializer.initAll()
    BreedAssetManager.initManager()
    SpellAssetManager.initManager()
    CharacterManager.initManager()
    ConfigManager.initManager()
    MetamobConfigManager.initManager()
    HintManager.initManager()
    CustomTransitionManager.initManager()
    MainFrame.isVisible = true
}