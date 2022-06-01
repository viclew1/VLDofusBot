package fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit

import fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.spells.SpellBarEditionPanel
import fr.lewon.dofus.bot.gui.vldb.panes.character.card.edit.spells.visual.SpellVisualPanel
import fr.lewon.dofus.bot.model.characters.DofusBreedAssets
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class CharacterFormPanel(character: DofusCharacter, onSaveAction: (DofusCharacter) -> Unit) :
    JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    companion object {
        const val CONNECTION_INFO_WIDTH = 250
        const val CONNECTION_INFO_HEIGHT = 300

        const val SPELL_VISUAL_WIDTH = 270
        const val SPELL_VISUAL_HEIGHT = 300

        const val SPELLS_EDITOR_WIDTH = 520
    }

    private val spells = ArrayList(character.characterSpells.map { it.copy() })
    private val editCoPanel = EditCharacterConnectionInfoPanel(character, spells, onSaveAction)
    private val spellVisualPanel = SpellVisualPanel()
    private val spellBarEditionPanel = SpellBarEditionPanel(spells, spellVisualPanel)

    init {
        editCoPanel.classComboBox.addItemListener {
            val dofusBreedAssets = editCoPanel.classComboBox.selectedItem as DofusBreedAssets
            spellBarEditionPanel.updateBreed(dofusBreedAssets.breed.id)
        }
        spellBarEditionPanel.updateBreed(character.dofusClassId)

        val topPanel = JPanel(MigLayout("Insets 0, gapX 0, gapY 0"))
        val connectionInfoWidth = "$CONNECTION_INFO_WIDTH:$CONNECTION_INFO_WIDTH:$CONNECTION_INFO_WIDTH"
        val connectionInfoHeight = "$CONNECTION_INFO_HEIGHT:$CONNECTION_INFO_HEIGHT:$CONNECTION_INFO_HEIGHT"
        val editCoPanelConstraints = "w $connectionInfoWidth, h $connectionInfoHeight"
        val spellVisualWidth = "$SPELL_VISUAL_WIDTH:$SPELL_VISUAL_WIDTH:$SPELL_VISUAL_WIDTH"
        val spellVisualHeight = "$SPELL_VISUAL_HEIGHT:$SPELL_VISUAL_HEIGHT:$SPELL_VISUAL_HEIGHT"
        spellVisualPanel.setSize(SPELL_VISUAL_WIDTH, SPELL_VISUAL_HEIGHT)
        val spellVisualPanelConstraints = "w $spellVisualWidth, h $spellVisualHeight"
        topPanel.add(editCoPanel, editCoPanelConstraints)
        topPanel.add(spellVisualPanel, spellVisualPanelConstraints)

        val w = SPELLS_EDITOR_WIDTH
        add(topPanel, "w $w:$w:$w, wrap")
        add(spellBarEditionPanel, "w $w:$w:$w, h max")

        spellVisualPanel.border = BorderFactory.createEtchedBorder()
        spellBarEditionPanel.border = BorderFactory.createEtchedBorder()
    }

}