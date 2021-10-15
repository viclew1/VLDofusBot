package fr.lewon.dofus.bot.gui.tabs.characters.card.listener

import fr.lewon.dofus.bot.gui.tabs.characters.card.CharacterCard
import fr.lewon.dofus.bot.gui.tabs.characters.card.CharacterCardList
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class CharacterCardListener(
    private val list: CharacterCardList,
    private val card: CharacterCard,
    private val dragManager: DragManager
) : MouseAdapter() {

    override fun mouseClicked(e: MouseEvent) {
        list.selectCharacter(card)
    }

    override fun mousePressed(e: MouseEvent) {
        dragManager.mousePressed(card)
    }

    override fun mouseEntered(e: MouseEvent) {
        dragManager.mouseEntered(card)
    }

    override fun mouseExited(e: MouseEvent) {
        dragManager.mouseExited()
    }

    override fun mouseReleased(e: MouseEvent) {
        dragManager.mouseReleased()
    }

    override fun mouseDragged(e: MouseEvent) {
        dragManager.mouseDragged()
    }
}