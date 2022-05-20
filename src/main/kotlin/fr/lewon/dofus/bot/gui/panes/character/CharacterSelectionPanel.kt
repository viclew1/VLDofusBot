package fr.lewon.dofus.bot.gui.panes.character

import fr.lewon.dofus.bot.gui.MainPanel
import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.custom.list.CardSelectionPanel
import fr.lewon.dofus.bot.gui.panes.character.card.CharacterCardList
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager

object CharacterSelectionPanel : CardSelectionPanel<DofusCharacter>("Characters") {

    override fun buildCardList(): CardList<DofusCharacter> {
        return CharacterCardList(MainPanel.CHARACTERS_WIDTH, this)
    }

    override fun processAddItemButton() {
        MainPanel.addCharacterEditTab(null) {
            val createdCharacter = CharacterManager.addCharacter(
                it.pseudo, it.dofusClassId, it.characterSpells
            )
            cardList.addItem(createdCharacter)
        }
    }

    override fun processUpdateItemButton(card: Card<DofusCharacter>) {
        MainPanel.addCharacterEditTab(card.item) {
            CharacterManager.updateCharacter(
                card.item, it.pseudo, it.dofusClassId, it.characterSpells
            )
            card.updateCard(card.item === cardList.selectedItem)
        }
    }

}