package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import net.miginfocom.swing.MigLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel

class CharacterFilterPanel : JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    private val checkboxAndLabelByCharacter = HashMap<DofusCharacter, Pair<JCheckBox, JLabel>>()

    fun addCharacter(character: DofusCharacter) {
        val checkBox = JCheckBox()
        checkBox.addItemListener { GlobalScriptPanel.selectorPanel.updateSelectedCharacters() }
        val label = JLabel("${character.pseudo} (${character.login})")
        checkboxAndLabelByCharacter[character] = checkBox to label
        add(checkBox)
        add(label, "wrap")
    }

    fun removeCharacter(character: DofusCharacter) {
        val checkboxAndLabel = checkboxAndLabelByCharacter[character] ?: return
        val checkbox = checkboxAndLabel.first
        val label = checkboxAndLabel.second
        remove(checkbox)
        remove(label)
    }

    fun getSelectedCharacters(): List<DofusCharacter> {
        return checkboxAndLabelByCharacter.filter { it.value.first.isSelected }.map { it.key }
    }

    fun getAllCheckboxes(): List<JCheckBox> {
        return checkboxAndLabelByCharacter.values.map { it.first }.toList()
    }
}