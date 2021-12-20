package fr.lewon.dofus.bot.gui.panes.character.card

import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.gui.panes.character.CharacterSelectionPanel
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import java.awt.Color
import java.awt.Font
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.*
import javax.swing.border.BevelBorder
import javax.swing.border.EmptyBorder


class CharacterCard(val character: DofusCharacter) : JPanel() {

    companion object {
        private const val BLUR_RATIO = 0.75f
        private const val FOCUS_BLUR_RATIO = 1.1f

        private const val BANNER_DELTA_WIDTH_RATIO = 1f / 5f
        private const val BANNER_WIDTH_RATIO = 1f + 2f * BANNER_DELTA_WIDTH_RATIO

        private const val ICON_DELTA_HEIGHT_RATIO = 1f / 4f
        private const val ICON_HEIGHT_RATIO = 1f + 2f * ICON_DELTA_HEIGHT_RATIO

        private const val BUTTON_HEIGHT_RATIO = 1f / 3f
        private const val BUTTON_DELTA_HEIGHT_RATIO = 1f / 4f

        private const val LABEL_DELTA_HEIGHT_RATIO = 1f / 6f
        private const val LABEL_HEIGHT_RATIO = 1f / 3f
        private const val LABEL_DELTA_WIDTH_RATIO = 1f / 4f
        private const val LABEL_WIDTH_RATIO = 1f - LABEL_DELTA_WIDTH_RATIO
    }

    private val iconLabel = JLabel()
    private val backgroundLabel = JLabel()
    private val loginLabel = OutlineJLabel()
    private val pseudoLabel = OutlineJLabel()
    private val deleteButton = JButton()
    private val editButton = JButton()

    private var selected = false
    private var cardWidth = 0
    private var cardHeight = 0
    private var dofusClass = character.dofusClass
    private lateinit var bgImg: BufferedImage
    private lateinit var blurredImg: BufferedImage

    init {
        isOpaque = true
        layout = null
        backgroundLabel.verticalAlignment = SwingConstants.TOP
        border = BevelBorder(BevelBorder.RAISED)

        editButton.isVisible = false
        deleteButton.isVisible = false

        val loginFont = Font("Impact", Font.PLAIN, 16)
        val pseudoFont = Font("Impact", Font.PLAIN, 20)

        loginLabel.font = loginFont
        loginLabel.foreground = Color.LIGHT_GRAY
        loginLabel.border = EmptyBorder(3, 3, 3, 3)
        pseudoLabel.font = pseudoFont
        pseudoLabel.foreground = Color.WHITE
        pseudoLabel.border = EmptyBorder(3, 3, 3, 3)

        loginLabel.horizontalAlignment = SwingConstants.LEFT
        loginLabel.verticalAlignment = SwingConstants.CENTER
        pseudoLabel.horizontalAlignment = SwingConstants.LEFT
        pseudoLabel.verticalAlignment = SwingConstants.CENTER

        val buttonMouseListener = object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                deleteButton.isVisible = true
                editButton.isVisible = true
            }

            override fun mouseExited(e: MouseEvent) {
                deleteButton.isVisible = false
                editButton.isVisible = false
            }
        }
        addMouseListener(buttonMouseListener)

        deleteButton.isBorderPainted = false
        deleteButton.border = null
        deleteButton.margin = Insets(0, 0, 0, 0)
        deleteButton.isContentAreaFilled = false
        deleteButton.addActionListener { CharacterSelectionPanel.deleteCharacter(this) }
        deleteButton.addMouseListener(buttonMouseListener)

        editButton.isBorderPainted = false
        editButton.border = null
        editButton.margin = Insets(0, 0, 0, 0)
        editButton.isContentAreaFilled = false
        editButton.addActionListener { CharacterSelectionPanel.updateCharacter(this) }
        editButton.addMouseListener(buttonMouseListener)


        add(deleteButton)
        add(editButton)
        add(loginLabel)
        add(pseudoLabel)
        add(iconLabel)
        add(backgroundLabel)
    }

    private fun updateLogin(text: String, x: Int, y: Int, w: Int, h: Int) {
        loginLabel.text = text
        loginLabel.setBounds(x, y, w, h)
    }

    private fun updatePseudo(text: String, x: Int, y: Int, w: Int, h: Int) {
        pseudoLabel.text = text
        pseudoLabel.setBounds(x, y, w, h)
    }

    fun initialize(selected: Boolean, w: Int, h: Int) {
        setSize(w, h)
        cardWidth = w
        cardHeight = h
        val iconImg =
            ImageUtil.getScaledImageKeepHeight(character.dofusClass.iconData, (cardHeight * ICON_HEIGHT_RATIO).toInt())
        iconLabel.setBounds(
            (-cardHeight * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (-cardHeight * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (cardHeight * ICON_HEIGHT_RATIO).toInt(),
            (cardHeight * ICON_HEIGHT_RATIO).toInt()
        )
        iconLabel.icon = ImageIcon(iconImg)
        backgroundLabel.setBounds(
            (-cardWidth * BANNER_DELTA_WIDTH_RATIO).toInt(),
            -5,
            (cardWidth * BANNER_WIDTH_RATIO).toInt(),
            cardHeight + 5
        )

        updateLogin(
            character.login, (cardWidth * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (cardHeight * LABEL_DELTA_HEIGHT_RATIO).toInt(),
            (cardWidth * LABEL_WIDTH_RATIO).toInt(),
            (cardHeight * LABEL_HEIGHT_RATIO).toInt()
        )
        updatePseudo(
            character.pseudo,
            (cardWidth * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (cardHeight * (LABEL_HEIGHT_RATIO + LABEL_DELTA_HEIGHT_RATIO)).toInt(),
            (cardWidth * LABEL_WIDTH_RATIO).toInt(),
            (cardHeight * LABEL_HEIGHT_RATIO).toInt()
        )

        val buttonSz = (cardHeight * BUTTON_HEIGHT_RATIO).toInt()
        val buttonDelta = (BUTTON_DELTA_HEIGHT_RATIO * cardHeight).toInt()
        editButton.icon = ImageIcon(ImageUtil.getScaledImage(UiResource.EDIT.imageData, buttonSz, buttonSz))
        editButton.rolloverIcon =
            ImageIcon(ImageUtil.getScaledImage(UiResource.EDIT.filledImageData, buttonSz, buttonSz))
        editButton.setBounds(cardWidth - buttonSz - buttonDelta, 0, buttonSz, buttonSz)

        deleteButton.icon = ImageIcon(ImageUtil.getScaledImage(UiResource.DELETE.imageData, buttonSz, buttonSz))
        deleteButton.rolloverIcon =
            ImageIcon(ImageUtil.getScaledImage(UiResource.DELETE.filledImageData, buttonSz, buttonSz))
        deleteButton.setBounds(cardWidth - buttonSz - buttonDelta, buttonSz, buttonSz, buttonSz)

        updateBgImg()
        updateBlurredImg()
        update(selected)
    }

    fun update(selected: Boolean) {
        if (character.dofusClass != dofusClass) {
            dofusClass = character.dofusClass
            updateBgImg()
            this.selected = selected
            updateBlurredImg()
        } else if (this.selected != selected) {
            this.selected = selected
            updateBlurredImg()
        }

        loginLabel.text = character.login
        pseudoLabel.text = character.pseudo
    }

    private fun updateBgImg() {
        bgImg = ImageUtil.getScaledImage(dofusClass.bannerData, (cardWidth * BANNER_WIDTH_RATIO).toInt())
    }

    private fun updateBlurredImg() {
        blurredImg = ImageUtil.blurImage(bgImg, getBlurRatio())
        backgroundLabel.icon = ImageIcon(blurredImg)
    }

    private fun getBlurRatio(): Float {
        return if (selected) FOCUS_BLUR_RATIO else BLUR_RATIO
    }

}