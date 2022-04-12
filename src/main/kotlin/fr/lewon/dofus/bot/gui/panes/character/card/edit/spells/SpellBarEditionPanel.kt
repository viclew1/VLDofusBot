package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells

import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.visual.SpellVisualPanel
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import net.miginfocom.swing.MigLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel

class SpellBarEditionPanel(
    private val spells: ArrayList<CharacterSpell>,
    private val spellVisualPanel: SpellVisualPanel
) : JPanel(MigLayout()) {

    private val spellCellPanels = ArrayList<SpellCellPanel>()
    private var selectedSpell: SpellCellPanel? = null

    init {
        val titleLb = JLabel("Spells")
        titleLb.font = AppFonts.TITLE_FONT
        add(titleLb, "pad 0 5, alignY center, wrap")

        addSpells(false)
        addSpells(true)
        updateSelectedSpell(spellCellPanels.firstOrNull { it.characterSpell.spell != null })
    }

    private fun addSpells(ctrlModifier: Boolean) {
        val constraints = "w 46:46:46, h 46:46:46"
        for (j in 1..9) {
            addSpellCellPanel(SpellCellPanel(getSpell(j, ctrlModifier)), constraints)
        }
        addSpellCellPanel(SpellCellPanel(getSpell(0, ctrlModifier)), "$constraints, wrap")
    }

    private fun addSpellCellPanel(spellCellPanel: SpellCellPanel, constraints: String) {
        spellCellPanel.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent?) {
                updateSelectedSpell(spellCellPanel)
            }
        })
        spellCellPanels.add(spellCellPanel)
        add(spellCellPanel, constraints)
    }

    private fun getSpell(key: Int, ctrlModifier: Boolean): CharacterSpell {
        return spells.firstOrNull { it.key == key.digitToChar() && it.ctrlModifier == ctrlModifier }
            ?: CharacterSpell(null, key.digitToChar(), ctrlModifier).also { spells.add(it) }
    }

    private fun updateSelectedSpell(spellCellPanel: SpellCellPanel?) {
        selectedSpell?.isSelected = false
        selectedSpell = spellCellPanel
        selectedSpell?.isSelected = true
        spellVisualPanel.visualizeSpell(selectedSpell?.characterSpell?.spell)
    }

    fun updateBreed(breedId: Int) {
        spellCellPanels.forEach { it.updatePopupMenu(breedId) }
    }

}