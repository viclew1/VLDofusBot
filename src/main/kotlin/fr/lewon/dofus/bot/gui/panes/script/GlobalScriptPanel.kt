package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.panes.script.selector.GlobalScriptSelectorPanel
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class GlobalScriptPanel : JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    init {
        val characters = CharacterManager.getCharacters()
        val characterFilterPanel = CharacterFilterPanel(characters)
        val selectorPanel = GlobalScriptSelectorPanel(characters, characterFilterPanel)
        characterFilterPanel.getAllCheckboxes().forEach {
            it.addItemListener { selectorPanel.updateSelectedCharacters() }
        }
        add(selectorPanel, "w 340:340:340, h 0:max:max")
        add(characterFilterPanel, "w 0:max:max, h 0:max:max")
        selectorPanel.border = BorderFactory.createEtchedBorder()
        characterFilterPanel.border = BorderFactory.createEtchedBorder()
    }

}