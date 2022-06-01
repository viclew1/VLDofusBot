package fr.lewon.dofus.bot.gui.vldb.panes.character.card

import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.vldb.VldbMainPanel
import fr.lewon.dofus.bot.gui.vldb.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager


class CharacterCardList(cardWidth: Int, characterSelectionPanel: CharacterSelectionPanel) : CardList<DofusCharacter>(
    cardWidth,
    cardWidth / 6,
    ArrayList(CharacterManager.getCharacters()),
    characterSelectionPanel,
    gapY = 3,
    initialSelectedItem = null
) {

    override fun buildCard(item: DofusCharacter): Card<DofusCharacter> {
        return CharacterCard(this, item)
    }

    override fun onItemRemove(item: DofusCharacter) {
        CharacterManager.removeCharacter(item)
    }

    override fun onCardSelect(card: Card<DofusCharacter>?) {
        card?.let {
            VldbMainPanel.addCharacterScriptTab(it.item)
        }
    }

    override fun onItemMoved(item: DofusCharacter, fromIndex: Int, toIndex: Int) {
        CharacterManager.moveCharacter(item, toIndex)
    }
}