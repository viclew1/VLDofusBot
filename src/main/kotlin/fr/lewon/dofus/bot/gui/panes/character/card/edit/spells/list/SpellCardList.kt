package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.list

import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.SpellSelectionPanel
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell

class SpellCardList(
    cardWidth: Int,
    private val spellSelectionPanel: SpellSelectionPanel,
    spells: ArrayList<CharacterSpell>
) : CardList<CharacterSpell>(
    cardWidth, cardWidth / 8, spells, spellSelectionPanel
) {

    override fun buildCard(item: CharacterSpell): SpellCard {
        return SpellCard(this, item)
    }

    override fun onItemRemove(item: CharacterSpell) {
        // Nothing
    }

    override fun onCardSelect(card: Card<CharacterSpell>?) {
        spellSelectionPanel.updateSpellPanels(card)
    }

    override fun onItemMoved(item: CharacterSpell) {
        // Nothing
    }
}