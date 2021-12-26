package fr.lewon.dofus.bot.gui.panes.character.card

import fr.lewon.dofus.bot.gui.MainPanel
import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager


class CharacterCardList(cardWidth: Int, characterSelectionPanel: CharacterSelectionPanel) : CardList<DofusCharacter>(
    cardWidth,
    cardWidth / 4,
    ArrayList(CharacterManager.getCharacters()),
    characterSelectionPanel,
    gapY = 3
) {

    override fun buildCard(item: DofusCharacter): Card<DofusCharacter> {
        return CharacterCard(this, item)
    }

    override fun onItemRemove(item: DofusCharacter) {
        CharacterManager.removeCharacter(item)
    }

    override fun onCardSelect(card: Card<DofusCharacter>?) {
        card?.let {
            MainPanel.addCharacterScriptTab(it.item)
        }
    }

}