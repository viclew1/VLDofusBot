package fr.lewon.dofus.bot.gui.vldb.panes.script

import fr.lewon.dofus.bot.gui.vldb.panes.script.selector.GlobalScriptSelectorPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.impl.listeners.CharacterManagerListener
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

object GlobalScriptPanel : JPanel(MigLayout("insets 0, gapX 0, gapY 0")), CharacterManagerListener {

    val characterFilterPanel = CharacterFilterPanel()
    val selectorPanel = GlobalScriptSelectorPanel()

    init {
        for (character in CharacterManager.getCharacters()) {
            characterFilterPanel.addCharacter(character)
            selectorPanel.addListenedCharacter(character)
        }
        CharacterManager.addListener(this)
        add(selectorPanel, "w 340:340:340, h 0:max:max")
        add(characterFilterPanel, "w 0:max:max, h 0:max:max")
        selectorPanel.border = BorderFactory.createEtchedBorder()
        characterFilterPanel.border = BorderFactory.createEtchedBorder()
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