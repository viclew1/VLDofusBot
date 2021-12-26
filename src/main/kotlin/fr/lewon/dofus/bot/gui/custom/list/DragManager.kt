package fr.lewon.dofus.bot.gui.custom.list

class DragManager<T>(private val list: CardList<T>) {

    private var pressed: Boolean = false
    private var fromCard: Card<T>? = null
    private var toCard: Card<T>? = null

    fun mousePressed(card: Card<T>) {
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

    fun mouseEntered(card: Card<T>) {
        toCard = card
    }

    fun mouseExited() {
        if (!pressed) fromCard = null
    }

}