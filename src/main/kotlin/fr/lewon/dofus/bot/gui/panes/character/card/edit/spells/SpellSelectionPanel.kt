package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells

import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.custom.list.CardSelectionPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.GlobalCharacterFormPanel
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.list.SpellCardList
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.visual.SpellVisualPanel
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell

class SpellSelectionPanel(
    private val spells: ArrayList<CharacterSpell>,
    private val editSpellPanel: EditSpellPanel,
    private val spellVisualPanel: SpellVisualPanel
) : CardSelectionPanel<CharacterSpell>("Spells") {

    override fun buildCardList(): CardList<CharacterSpell> {
        return SpellCardList(GlobalCharacterFormPanel.SPELL_LIST_WIDTH, this, spells)
    }

    override fun processAddItemButton() {
        val card = cardList.addItem(CharacterSpell())
        cardList.selectItem(card)
        editSpellPanel.updateCharacterSpell(card.item) { onSpellUpdate(card) }
        spellVisualPanel.visualizeSpell(card.item.spell)
    }

    override fun processUpdateItemButton(card: Card<CharacterSpell>) {
        updateSpellPanels(card)
    }

    fun updateSpellPanels(card: Card<CharacterSpell>?) {
        editSpellPanel.updateCharacterSpell(card?.item) { onSpellUpdate(card) }
        spellVisualPanel.visualizeSpell(card?.item?.spell)
    }

    fun forceUpdateSpell() {
        val selectedItem = cardList.selectedItem
            ?: return
        val card = cardList.getCard(selectedItem)
        editSpellPanel.updateCharacterSpell(card?.item) { onSpellUpdate(card) }
        for (item in cardList.items) {
            onSpellUpdate(cardList.getCard(item))
        }
    }

    private fun onSpellUpdate(card: Card<CharacterSpell>?) {
        card?.updateCard(card.item === cardList.selectedItem)
        spellVisualPanel.visualizeSpell(card?.item?.spell)
    }
}