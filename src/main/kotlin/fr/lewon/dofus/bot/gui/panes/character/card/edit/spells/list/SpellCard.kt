package fr.lewon.dofus.bot.gui.panes.character.card.edit.spells.list

import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder

class SpellCard(
    spellCardList: SpellCardList,
    spellCombination: SpellCombination
) : Card<SpellCombination>(spellCardList, spellCombination) {

    companion object {
        private const val ICON_DELTA_HEIGHT_RATIO = -1f / 4f
        private const val ICON_HEIGHT_RATIO = 1f + 2f * ICON_DELTA_HEIGHT_RATIO

        private const val LABEL_DELTA_HEIGHT_RATIO = 1f / 6f
        private const val LABEL_HEIGHT_RATIO = 1f / 3f
        private const val LABEL_DELTA_WIDTH_RATIO = 1f / 4f
        private const val LABEL_WIDTH_RATIO = 1f - LABEL_DELTA_WIDTH_RATIO
    }

    private val iconLabel = JLabel()
    private val keyLabel = OutlineJLabel()
    private val rangeLabel = OutlineJLabel()

    init {
        isOpaque = true
        layout = null
        border = BorderFactory.createRaisedSoftBevelBorder()

        val spellTypeFont = Font("Impact", Font.PLAIN, 16)
        val textFont = Font("Impact", Font.PLAIN, 17)

        keyLabel.font = spellTypeFont
        keyLabel.foreground = Color.LIGHT_GRAY
        keyLabel.border = EmptyBorder(3, 3, 3, 3)
        rangeLabel.font = textFont
        rangeLabel.foreground = Color.WHITE
        rangeLabel.border = EmptyBorder(3, 3, 3, 3)

        keyLabel.horizontalAlignment = SwingConstants.LEFT
        keyLabel.verticalAlignment = SwingConstants.CENTER
        rangeLabel.horizontalAlignment = SwingConstants.LEFT
        rangeLabel.verticalAlignment = SwingConstants.CENTER
    }

    override fun hasEditButton(): Boolean {
        return false
    }

    override fun initializeCard(selected: Boolean) {
        add(rangeLabel)
        add(keyLabel)
        add(iconLabel)

        iconLabel.setBounds(
            (-height * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (-height * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (height * ICON_HEIGHT_RATIO).toInt(),
            (height * ICON_HEIGHT_RATIO).toInt()
        )

        keyLabel.setBounds(
            (width * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (height * LABEL_DELTA_HEIGHT_RATIO).toInt(),
            (width * LABEL_WIDTH_RATIO).toInt(),
            (height * LABEL_HEIGHT_RATIO).toInt()
        )
        rangeLabel.setBounds(
            (width * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (height * (LABEL_HEIGHT_RATIO + LABEL_DELTA_HEIGHT_RATIO)).toInt(),
            (width * LABEL_WIDTH_RATIO).toInt(),
            (height * LABEL_HEIGHT_RATIO).toInt()
        )

        updateCard(selected)
    }

    override fun updateCard(selected: Boolean) {
        background = if (selected) Color.GRAY else null
        refreshLabels()
        refreshIcon()
    }

    private fun refreshIcon() {
        val iconImg = ImageUtil.getScaledImageKeepHeight(
            item.type.getListImageIcon(), (height * ICON_HEIGHT_RATIO).toInt()
        )
        iconLabel.icon = ImageIcon(iconImg)
    }

    private fun refreshLabels() {
        rangeLabel.text = "Range : ${item.minRange} to ${item.maxRange}"
        if (item.modifiableRange) {
            rangeLabel.text += " (Modifiable)"
        }
        keyLabel.text = "Keys : ${item.keys}"
    }
}