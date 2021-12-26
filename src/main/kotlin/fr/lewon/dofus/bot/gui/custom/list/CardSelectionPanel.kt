package fr.lewon.dofus.bot.gui.custom.list

import fr.lewon.dofus.bot.gui.util.AppFonts
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

abstract class CardSelectionPanel<T>(
    panelName: String
) : JPanel(MigLayout("insets 0, fill")) {

    lateinit var cardList: CardList<T>

    private val addItemButton = JButton("+")

    init {
        val titleLb = JLabel(panelName)
        titleLb.font = AppFonts.TITLE_FONT
        add(titleLb, "pad 0 5, alignY center")

        addItemButton.font = AppFonts.HEADER_FONT
        addItemButton.isBorderPainted = false
        addItemButton.border = null
        addItemButton.margin = Insets(0, 0, 0, 0)
        addItemButton.isContentAreaFilled = false
        addItemButton.addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                addItemButton.foreground = Color.BLACK
            }

            override fun mouseExited(e: MouseEvent) {
                addItemButton.foreground = null
            }
        })
        add(addItemButton, "al right, wrap")
        addItemButton.addActionListener { processAddItemButton() }
        SwingUtilities.invokeLater { initAll() }
    }

    private fun initAll() {
        cardList = buildCardList()
        val scrollPane = JScrollPane(cardList)
        scrollPane.verticalScrollBar.unitIncrement *= 5
        scrollPane.horizontalScrollBar = null
        scrollPane.border = BorderFactory.createEtchedBorder()
        add(scrollPane, "span 2 1, width max, height max")
    }

    abstract fun buildCardList(): CardList<T>

    abstract fun processAddItemButton()

    abstract fun processUpdateItemButton(card: Card<T>)

}