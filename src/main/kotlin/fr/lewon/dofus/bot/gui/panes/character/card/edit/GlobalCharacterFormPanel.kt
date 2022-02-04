package fr.lewon.dofus.bot.gui.panes.character.card.edit

import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager
import fr.lewon.dofus.bot.core.model.charac.DofusBreed
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.EditSpellPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.SpellSelectionPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.visual.SpellVisualPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.spells.SpellType
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class GlobalCharacterFormPanel(character: DofusCharacter, onSaveAction: (DofusCharacter) -> Unit) :
    JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    companion object {
        const val CONNECTION_INFO_WIDTH = 250
        const val CONNECTION_INFO_HEIGHT = 300

        const val SPELL_VISUAL_WIDTH = 250
        const val SPELL_VISUAL_HEIGHT = 300

        const val SPELL_LIST_WIDTH = 500
    }

    private val spells = ArrayList(character.characterSpells.map { it.copy() })
    private val editCoPanel = EditCharacterConnectionInfoPanel(
        character, spells, onSaveAction, this::updateCharacterSpells
    )
    private val editSpellPanel = EditSpellPanel(getAvailableSpellsByName(character.dofusClassId))
    private val spellVisualPanel = SpellVisualPanel()
    private val spellSelectionPanel = SpellSelectionPanel(spells, editSpellPanel, spellVisualPanel)

    init {
        val leftTopPanel = JPanel(MigLayout("Insets 0, gapX 0, gapY 0"))
        val connectionInfoWidth = "$CONNECTION_INFO_WIDTH:$CONNECTION_INFO_WIDTH:$CONNECTION_INFO_WIDTH"
        val connectionInfoHeight = "$CONNECTION_INFO_HEIGHT:$CONNECTION_INFO_HEIGHT:$CONNECTION_INFO_HEIGHT"
        val editCoPanelConstraints = "w $connectionInfoWidth, h $connectionInfoHeight"
        val spellVisualWidth = "$SPELL_VISUAL_WIDTH:$SPELL_VISUAL_WIDTH:$SPELL_VISUAL_WIDTH"
        val spellVisualHeight = "$SPELL_VISUAL_HEIGHT:$SPELL_VISUAL_HEIGHT:$SPELL_VISUAL_HEIGHT"
        spellVisualPanel.setSize(SPELL_VISUAL_WIDTH, SPELL_VISUAL_HEIGHT)
        val spellVisualPanelConstraints = "w $spellVisualWidth, h $spellVisualHeight"
        leftTopPanel.add(editCoPanel, editCoPanelConstraints)
        leftTopPanel.add(spellVisualPanel, spellVisualPanelConstraints)

        val leftPanel = JPanel(MigLayout("Insets 0, gapX 0, gapY 0"))
        leftPanel.add(leftTopPanel, "wrap")
        leftPanel.add(spellSelectionPanel, "h max")
        val spellListWidth = "$SPELL_LIST_WIDTH:$SPELL_LIST_WIDTH:$SPELL_LIST_WIDTH"
        add(leftPanel, "w $spellListWidth, h max")
        add(editSpellPanel, "w max, h max")

        spellVisualPanel.border = BorderFactory.createEtchedBorder()
        spellSelectionPanel.border = BorderFactory.createEtchedBorder()
        editSpellPanel.border = BorderFactory.createEtchedBorder()
    }

    private fun updateCharacterSpells(breed: DofusBreed) {
        val availableSpellsByName = getAvailableSpellsByName(breed.id)
        editSpellPanel.availableSpellsByName = availableSpellsByName
        for (spell in spells) {
            if (spell.type == SpellType.NAMED_SPELL && !availableSpellsByName.values.contains(spell.spell)) {
                spell.spell = availableSpellsByName.values.minByOrNull { it.name }
                    ?: error("There should be at least one available spell")
            }
        }
        spellSelectionPanel.forceUpdateSpell()
    }

    private fun getAvailableSpellsByName(breedId: Int): Map<String, DofusSpell> {
        return SpellVariantManager.getSpellVariants(breedId)
            .flatMap { it.spells }
            .filter { it.levels.lastOrNull()?.effects?.isNotEmpty() ?: false }
            .associateBy { it.name }
    }

}