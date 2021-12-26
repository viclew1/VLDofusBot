package fr.lewon.dofus.bot.gui.panes.character.card

import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder


class CharacterCard(cardList: CharacterCardList, character: DofusCharacter) :
    Card<DofusCharacter>(cardList, character) {

    companion object {
        private const val BLUR_RATIO = 0.75f
        private const val FOCUS_BLUR_RATIO = 1.1f

        private const val BANNER_DELTA_WIDTH_RATIO = 1f / 5f
        private const val BANNER_WIDTH_RATIO = 1f + 2f * BANNER_DELTA_WIDTH_RATIO

        private const val ICON_DELTA_HEIGHT_RATIO = 1f / 4f
        private const val ICON_HEIGHT_RATIO = 1f + 2f * ICON_DELTA_HEIGHT_RATIO

        private const val LABEL_DELTA_HEIGHT_RATIO = 1f / 6f
        private const val LABEL_HEIGHT_RATIO = 1f / 3f
        private const val LABEL_DELTA_WIDTH_RATIO = 1f / 4f
        private const val LABEL_WIDTH_RATIO = 1f - LABEL_DELTA_WIDTH_RATIO
    }

    private val iconLabel = JLabel()
    private val backgroundLabel = JLabel()
    private val loginLabel = OutlineJLabel()
    private val pseudoLabel = OutlineJLabel()

    private var dofusClass = character.dofusClass
    private lateinit var bgImg: BufferedImage
    private lateinit var blurredImg: BufferedImage

    init {
        isOpaque = true
        layout = null
        backgroundLabel.verticalAlignment = SwingConstants.TOP
        border = BorderFactory.createRaisedBevelBorder()

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
    }

    override fun initializeCard(selected: Boolean) {
        add(loginLabel)
        add(pseudoLabel)
        add(iconLabel)
        add(backgroundLabel)
        
        val iconImg =
            ImageUtil.getScaledImageKeepHeight(item.dofusClass.iconData, (height * ICON_HEIGHT_RATIO).toInt())
        iconLabel.setBounds(
            (-height * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (-height * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (height * ICON_HEIGHT_RATIO).toInt(),
            (height * ICON_HEIGHT_RATIO).toInt()
        )
        iconLabel.icon = ImageIcon(iconImg)
        backgroundLabel.setBounds(
            (-width * BANNER_DELTA_WIDTH_RATIO).toInt(),
            -5,
            (width * BANNER_WIDTH_RATIO).toInt(),
            height + 5
        )

        updateLogin(
            item.login, (width * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (height * LABEL_DELTA_HEIGHT_RATIO).toInt(),
            (width * LABEL_WIDTH_RATIO).toInt(),
            (height * LABEL_HEIGHT_RATIO).toInt()
        )
        updatePseudo(
            item.pseudo,
            (width * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (height * (LABEL_HEIGHT_RATIO + LABEL_DELTA_HEIGHT_RATIO)).toInt(),
            (width * LABEL_WIDTH_RATIO).toInt(),
            (height * LABEL_HEIGHT_RATIO).toInt()
        )

        updateBgImg()
        updateBlurredImg(selected)
        updateCard(selected)
    }

    private fun updateLogin(text: String, x: Int, y: Int, w: Int, h: Int) {
        loginLabel.text = text
        loginLabel.setBounds(x, y, w, h)
    }

    private fun updatePseudo(text: String, x: Int, y: Int, w: Int, h: Int) {
        pseudoLabel.text = text
        pseudoLabel.setBounds(x, y, w, h)
    }

    override fun updateCard(selected: Boolean) {
        if (item.dofusClass != dofusClass) {
            dofusClass = item.dofusClass
            updateBgImg()
        }
        updateBlurredImg(selected)

        loginLabel.text = item.login
        pseudoLabel.text = item.pseudo
    }

    private fun updateBgImg() {
        bgImg = ImageUtil.getScaledImage(dofusClass.bannerData, (width * BANNER_WIDTH_RATIO).toInt())
    }

    private fun updateBlurredImg(selected: Boolean) {
        blurredImg = ImageUtil.blurImage(bgImg, getBlurRatio(selected))
        backgroundLabel.icon = ImageIcon(blurredImg)
    }

    private fun getBlurRatio(selected: Boolean): Float {
        return if (selected) FOCUS_BLUR_RATIO else BLUR_RATIO
    }

}