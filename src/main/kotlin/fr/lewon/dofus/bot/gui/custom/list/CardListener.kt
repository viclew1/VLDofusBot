package fr.lewon.dofus.bot.gui.custom.list

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class CardListener<T>(
    private val list: CardList<T>,
    private val card: Card<T>,
    private val dragManager: DragManager<T>
) : MouseAdapter() {

    override fun mouseClicked(e: MouseEvent) {
        list.selectItem(card)
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