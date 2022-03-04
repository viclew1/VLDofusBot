package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.panes.script.selector.GlobalScriptSelectorPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.listeners.CharacterManagerListener
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class GlobalScriptPanel : JPanel(MigLayout("insets 0, gapX 0, gapY 0")), CharacterManagerListener {

    private val characterFilterPanel = CharacterFilterPanel()
    private val selectorPanel = GlobalScriptSelectorPanel(characterFilterPanel)

    init {
        val characters = CharacterManager.getCharacters()
        for (character in characters) {
            characterFilterPanel.addCharacter(character)
            selectorPanel.addListenedCharacter(character)
        }
        characterFilterPanel.getAllCheckboxes().forEach {
            it.addItemListener { selectorPanel.updateSelectedCharacters() }
        }
        add(selectorPanel, "w 340:340:340, h 0:max:max")
        add(characterFilterPanel, "w 0:max:max, h 0:max:max")
        selectorPanel.border = BorderFactory.createEtchedBorder()
        characterFilterPanel.border = BorderFactory.createEtchedBorder()
        CharacterManager.addListener(this)
    }

    override fun onCharacterCreate(character: DofusCharacter) {
        characterFilterPanel.addCharacter(character)
        selectorPanel.addListenedCharacter(character)
    }

    override fun onCharacterMove(character: DofusCharacter, toIndex: Int) {
        // Nothing
    }

    override fun onCharacterDelete(character: DofusCharacter) {
        characterFilterPanel.removeCharacter(character)
        selectorPanel.removeListenedCharacter(character)
    }

}