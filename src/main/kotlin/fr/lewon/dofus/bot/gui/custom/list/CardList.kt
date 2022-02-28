package fr.lewon.dofus.bot.gui.custom.list

import net.miginfocom.swing.MigLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities

abstract class CardList<T>(
    private val cardWidth: Int,
    private val cardHeight: Int,
    val items: ArrayList<T>,
    val cardSelectionPanel: CardSelectionPanel<T>,
    initialSelectedItem: T? = items.firstOrNull(),
    gapY: Int = 0
) : JPanel(MigLayout("gapX 0, gapY $gapY, insets 0")) {

    var selectedItem: T? = null
        private set
    private val dragManager = DragManager(this)
    private val cards = ArrayList<Card<T>>()

    init {
        for (item in items) {
            addCard(item)
        }
        SwingUtilities.invokeLater {
            initialSelectedItem?.let {
                selectItem(getCard(it))
            }
        }
    }

    fun getCard(item: T): Card<T>? {
        return cards.firstOrNull { it.item === item }
    }

    abstract fun buildCard(item: T): Card<T>

    abstract fun onItemRemove(item: T)

    abstract fun onItemMoved(item: T, fromIndex: Int, toIndex: Int)

    abstract fun onCardSelect(card: Card<T>?)

    fun addItem(item: T): Card<T> {
        items.add(item)
        return addCard(item)
    }

    private fun addCard(item: T): Card<T> {
        val card = buildCard(item)
        val selected = selectedItem === item
        card.initializeCard(selected, cardWidth, cardHeight)
        val listener = CardListener(this, card, dragManager)
        card.addMouseListener(listener)
        card.addMouseMotionListener(listener)
        cards.add(card)
        insertCard(card)
        updateUI()
        return card
    }

    fun selectItem(card: Card<T>?) {
        val item = card?.item
        onCardSelect(card)
        selectedItem = item
        cards.forEach { it.updateCard(false) }
        card?.updateCard(true)
        updateUI()
    }

    fun removeCard(card: Card<T>) {
        onItemRemove(card.item)
        cards.remove(card)
        items.remove(card.item)
        remove(card)
        if (card.item === selectedItem) {
            selectItem(null)
        } else {
            updateUI()
        }
    }

    private fun insertCard(card: Card<T>, index: Int = -1) {
        add(card, "width $cardWidth, height $cardHeight, wrap", index)
    }

    fun moveElement(fromCard: Card<T>, toCard: Card<T>) {
        val fromIndex = cards.indexOf(fromCard)
        val toIndex = cards.indexOf(toCard)
        if (fromIndex != toIndex) {
            cards.remove(fromCard)
            cards.add(toIndex, fromCard)
            items.removeAt(fromIndex)
            items.add(toIndex, fromCard.item)
            remove(fromCard)
            insertCard(fromCard, toIndex)
            updateUI()
            onItemMoved(fromCard.item, fromIndex, toIndex)
        }
    }

}