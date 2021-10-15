package fr.lewon.dofus.bot.gui.tabs.characters.card.listener

import fr.lewon.dofus.bot.gui.tabs.characters.card.CharacterCard
import fr.lewon.dofus.bot.gui.tabs.characters.card.CharacterCardList

class DragManager(private val list: CharacterCardList) {

    private var pressed: Boolean = false
    private var fromCard: CharacterCard? = null
    private var toCard: CharacterCard? = null

    fun mousePressed(card: CharacterCard) {
        pressed = true
        fromCard = card
    }

    fun mouseReleased() {
        pressed = false
    }

    fun mouseDragged() {
        val fromCard = fromCard ?: return
        val toCard = toCard ?: return
        list.moveElement(fromCard, toCard)
    }

    fun mouseEntered(card: CharacterCard) {
        toCard = card
    }

    fun mouseExited() {
        if (!pressed) fromCard = null
    }

}