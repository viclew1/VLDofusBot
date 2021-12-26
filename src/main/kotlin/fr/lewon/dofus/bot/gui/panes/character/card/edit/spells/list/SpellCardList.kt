package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.list

import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.SpellSelectionPanel
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination

class SpellCardList(
    cardWidth: Int,
    private val spellSelectionPanel: SpellSelectionPanel,
    spells: ArrayList<SpellCombination>
) : CardList<SpellCombination>(
    cardWidth, cardWidth / 8, spells, spellSelectionPanel
) {

    override fun buildCard(item: SpellCombination): SpellCard {
        return SpellCard(this, item)
    }

    override fun onItemRemove(item: SpellCombination) {
        // Nothing
    }

    override fun onCardSelect(card: Card<SpellCombination>?) {
        spellSelectionPanel.updateSpellPanels(card)
    }

}