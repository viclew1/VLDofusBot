package fr.lewon.dofus.bot.gui.vldb.panes.character

import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardList
import fr.lewon.dofus.bot.gui.custom.list.CardSelectionPanel
import fr.lewon.dofus.bot.gui.vldb.VldbMainPanel
import fr.lewon.dofus.bot.gui.vldb.panes.character.card.CharacterCardList
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.impl.CharacterManager

object CharacterSelectionPanel : CardSelectionPanel<DofusCharacter>("Characters") {

    override fun buildCardList(): CardList<DofusCharacter> {
        return CharacterCardList(VldbMainPanel.CHARACTERS_WIDTH, this)
    }

    override fun processAddItemButton() {
        VldbMainPanel.addCharacterEditTab(null) {
            val createdCharacter = CharacterManager.addCharacter(
                it.pseudo, it.dofusClassId, it.characterSpells
            )
            cardList.addItem(createdCharacter)
        }
    }

    override fun processUpdateItemButton(card: Card<DofusCharacter>) {
        VldbMainPanel.addCharacterEditTab(card.item) {
            CharacterManager.updateCharacter(
                card.item, it.pseudo, it.dofusClassId, it.characterSpells
            )
            card.updateCard(card.item === cardList.selectedItem)
        }
    }

}