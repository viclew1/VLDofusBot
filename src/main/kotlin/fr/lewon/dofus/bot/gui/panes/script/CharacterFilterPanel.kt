package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import net.miginfocom.swing.MigLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel

class CharacterFilterPanel(characters: List<DofusCharacter>) : JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    private val checkboxByCharacter = characters.associateWith { JCheckBox() }

    init {
        checkboxByCharacter.entries.forEach {
            add(it.value)
            add(JLabel("${it.key.pseudo} (${it.key.login})"), "wrap")
        }
    }

    fun getSelectedCharacters(): List<DofusCharacter> {
        return checkboxByCharacter.filter { it.value.isSelected }.map { it.key }
    }

    fun getAllCheckboxes(): List<JCheckBox> {
        return checkboxByCharacter.values.toList()
    }
}