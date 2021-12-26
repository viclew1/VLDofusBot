package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells

import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.custom.list.CardSelectionPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.GlobalCharacterFormPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.list.SpellCardList
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.visual.SpellVisualPanel
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

class SpellSelectionPanel(
    private val spells: ArrayList<SpellCombination>,
    private val editSpellPanel: EditSpellPanel,
    private val spellVisualPanel: SpellVisualPanel
) : CardSelectionPanel<SpellCombination>("Spells") {

    override fun buildCardList(): CardList<SpellCombination> {
        return SpellCardList(GlobalCharacterFormPanel.SPELL_LIST_WIDTH, this, spells)
    }

    override fun processAddItemButton() {
        val card = cardList.addItem(SpellCombination())
        cardList.selectItem(card)
        editSpellPanel.updateSpellCombination(card.item) { onSpellUpdate(card) }
        spellVisualPanel.visualizeSpell(card.item)
    }

    override fun processUpdateItemButton(card: Card<SpellCombination>) {
        updateSpellPanels(card)
    }

    fun updateSpellPanels(card: Card<SpellCombination>?) {
        editSpellPanel.updateSpellCombination(card?.item) { onSpellUpdate(card) }
        spellVisualPanel.visualizeSpell(card?.item)
    }

    private fun onSpellUpdate(card: Card<SpellCombination>?) {
        card?.updateCard(card.item === cardList.selectedItem)
        spellVisualPanel.visualizeSpell(card?.item)
    }
}