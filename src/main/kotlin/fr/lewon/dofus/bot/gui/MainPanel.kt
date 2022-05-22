package fr.lewon.dofus.bot.gui

import com.formdev.flatlaf.ui.FlatTabbedPaneUI
import fr.lewon.dofus.bot.core.VldbCoreInitializer
import fr.lewon.dofus.bot.gui.about.AboutPanel
import fr.lewon.dofus.bot.gui.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.CharacterFormPanel
import fr.lewon.dofus.bot.gui.panes.config.ConfigPanel
import fr.lewon.dofus.bot.gui.panes.script.CharacterScriptPanel
import fr.lewon.dofus.bot.gui.panes.script.GlobalScriptPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.BreedAssetManager
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.filemanagers.HintManager
import net.miginfocom.swing.MigLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*

object MainPanel : JPanel(MigLayout("gapX 0, gapY 0, fill, insets 0")) {

    const val CHARACTERS_WIDTH = 260
    private const val CONFIG_TABS_HEIGHT = 210

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
            if (selectedComponent is CharacterScriptPanel) {
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

        SwingUtilities.invokeLater {
            mainTabbedPane.setUI(object : FlatTabbedPaneUI() {
                override fun createMouseListener(): MouseListener {
                    return buildHeaderListener()
                }
            })
            addTab("Global scripts") { GlobalScriptPanel }
        }
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
    CharacterManager.initManager()
    BreedAssetManager.initManager()
    ConfigManager.initManager()
    HintManager.initManager()
    VldbMainFrame.isVisible = true
}