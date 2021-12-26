package fr.lewon.dofus.bot.gui.custom.list

import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import java.awt.Insets
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

abstract class Card<T>(private val cardList: CardList<T>, val item: T) : JPanel() {

    companion object {
        private const val BUTTON_HEIGHT_RATIO = 1f / 3f
        private const val BUTTON_DELTA_X_HEIGHT_RATIO = 1f / 4f
        private const val BUTTON_DELTA_Y_HEIGHT_RATIO = 1f / 10f
    }

    protected open fun hasEditButton(): Boolean {
        return true
    }

    protected open fun hasDeleteButton(): Boolean {
        return true
    }

    abstract fun updateCard(selected: Boolean)

    fun initializeCard(selected: Boolean, width: Int, height: Int) {
        setSize(width, height)

        val buttonInfoList = ArrayList<CardButtonInfo>()
        if (hasDeleteButton()) {
            buttonInfoList.add(CardButtonInfo("Delete", UiResource.DELETE) {
                cardList.removeCard(this)
            })
        }
        if (hasEditButton()) {
            buttonInfoList.add(CardButtonInfo("Update", UiResource.EDIT) {
                cardList.cardSelectionPanel.processUpdateItemButton(this)
            })
        }

        val buttonMouseListener = object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                buttonInfoList.forEach { it.button.isVisible = true }
            }

            override fun mouseExited(e: MouseEvent) {
                buttonInfoList.forEach { it.button.isVisible = false }
            }
        }
        addMouseListener(buttonMouseListener)

        val buttonSz = (height * BUTTON_HEIGHT_RATIO).toInt()
        val buttonDeltaX = (BUTTON_DELTA_X_HEIGHT_RATIO * height).toInt()
        val buttonDeltaY = (BUTTON_DELTA_Y_HEIGHT_RATIO * height).toInt()

        for ((index, buttonInfo) in buttonInfoList.withIndex()) {
            val button = buttonInfo.button
            button.toolTipText = buttonInfo.title
            button.isVisible = false
            button.isOpaque = true
            button.isBorderPainted = false
            button.border = null
            button.margin = Insets(0, 0, 0, 0)
            button.isContentAreaFilled = false
            button.addActionListener(buttonInfo.actionListener)
            button.addMouseListener(buttonMouseListener)

            button.icon = ImageIcon(ImageUtil.getScaledImage(buttonInfo.uiResource.imageData, buttonSz, buttonSz))
            button.rolloverIcon = ImageIcon(
                ImageUtil.getScaledImage(buttonInfo.uiResource.filledImageData, buttonSz, buttonSz)
            )
            button.setBounds(width - (index + 1) * buttonSz - buttonDeltaX, buttonDeltaY, buttonSz, buttonSz)
            add(button)
        }

        initializeCard(selected)
    }

    abstract fun initializeCard(selected: Boolean)

    private class CardButtonInfo(
        val title: String,
        val uiResource: UiResource,
        val button: JButton = JButton(),
        val actionListener: ActionListener,
    )
}