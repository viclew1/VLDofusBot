package fr.lewon.dofus.bot.gui.vldb.panes.character.card

import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.gui.custom.list.Card
import fr.lewon.dofus.bot.gui.custom.list.CardButtonInfo
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.DofusBreedAssets
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.util.filemanagers.impl.BreedAssetManager
import fr.lewon.dofus.bot.util.network.GameSnifferListener
import fr.lewon.dofus.bot.util.network.GameSnifferUtil
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder


class CharacterCard(cardList: CharacterCardList, private val character: DofusCharacter) :
    Card<DofusCharacter>(cardList, character), ScriptRunnerListener, GameSnifferListener {

    companion object {
        private const val BLUR_RATIO = 0.75f
        private const val FOCUS_BLUR_RATIO = 1.1f

        private const val BANNER_DELTA_WIDTH_RATIO = 1f / 5f
        private const val BANNER_WIDTH_RATIO = 1f + 2f * BANNER_DELTA_WIDTH_RATIO

        private const val ICON_DELTA_HEIGHT_RATIO = 1f / 4f
        private const val ICON_HEIGHT_RATIO = 1f + 2f * ICON_DELTA_HEIGHT_RATIO

        private const val ACTIVITY_HEIGHT_RATIO = 2f / 5f

        private const val LABEL_DELTA_HEIGHT_RATIO = 1f / 6f
        private const val LABEL_HEIGHT_RATIO = 2f / 3f
        private const val LABEL_DELTA_WIDTH_RATIO = 1f / 5f
        private const val LABEL_WIDTH_RATIO = 1f - LABEL_DELTA_WIDTH_RATIO
    }

    private val iconLabel = JLabel()
    private val backgroundLabel = JLabel()
    private val pseudoLabel = OutlineJLabel()
    private val activityLabel = JLabel()

    private var dofusClassId = character.dofusClassId
    private lateinit var bgImg: BufferedImage
    private lateinit var blurredImg: BufferedImage
    private lateinit var currentCharacterState: CharacterState

    init {
        isOpaque = true
        layout = null
        backgroundLabel.verticalAlignment = SwingConstants.TOP
        border = BorderFactory.createRaisedBevelBorder()

        val pseudoFont = Font("Impact", Font.PLAIN, 20)

        pseudoLabel.font = pseudoFont
        pseudoLabel.foreground = Color.WHITE
        pseudoLabel.border = EmptyBorder(3, 3, 3, 3)

        pseudoLabel.horizontalAlignment = SwingConstants.LEFT
        pseudoLabel.verticalAlignment = SwingConstants.CENTER

        ScriptRunner.addListener(character, this)
        GameSnifferUtil.addListener(character, this)
    }

    override fun buildButtonInfoList(): List<CardButtonInfo> {
        val buttonInfoList = ArrayList<CardButtonInfo>()
        buttonInfoList.add(CardButtonInfo("Delete", UiResource.DELETE) {
            cardList.removeCard(this)
        })
        buttonInfoList.add(CardButtonInfo("Configure character", UiResource.CONFIGURE_CHARACTER) {
            cardList.cardSelectionPanel.processUpdateItemButton(this)
        })
        return buttonInfoList
    }

    override fun initializeCard(selected: Boolean) {
        add(pseudoLabel)
        add(activityLabel)
        add(iconLabel)
        add(backgroundLabel)

        iconLabel.setBounds(
            (-height * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (-height * ICON_DELTA_HEIGHT_RATIO).toInt(),
            (height * ICON_HEIGHT_RATIO).toInt(),
            (height * ICON_HEIGHT_RATIO).toInt()
        )
        backgroundLabel.setBounds(
            (-width * BANNER_DELTA_WIDTH_RATIO).toInt(),
            -5,
            (width * BANNER_WIDTH_RATIO).toInt(),
            height + 5
        )
        activityLabel.setBounds(
            (width - 2 * ACTIVITY_HEIGHT_RATIO * height - 5).toInt(),
            (height - ACTIVITY_HEIGHT_RATIO * height - 2).toInt(),
            (ACTIVITY_HEIGHT_RATIO * height).toInt(),
            (ACTIVITY_HEIGHT_RATIO * height).toInt()
        )

        updatePseudo(
            item.pseudo,
            (width * LABEL_DELTA_WIDTH_RATIO).toInt(),
            (height * LABEL_DELTA_HEIGHT_RATIO).toInt(),
            (width * LABEL_WIDTH_RATIO).toInt(),
            (height * LABEL_HEIGHT_RATIO).toInt()
        )

        val dofusClass = BreedAssetManager.getAssets(dofusClassId)
        updateBgImg(dofusClass)
        updateIconImg(dofusClass)
        updateCard(selected)
        setDefaultState()
    }

    private fun updateActivityIcon(resource: UiResource) {
        activityLabel.icon = ImageIcon(ImageUtil.getScaledImage(resource.imageData, activityLabel.bounds.width))
    }

    private fun updatePseudo(text: String, x: Int, y: Int, w: Int, h: Int) {
        pseudoLabel.text = text
        pseudoLabel.setBounds(x, y, w, h)
    }

    override fun updateCard(selected: Boolean) {
        if (item.dofusClassId != dofusClassId) {
            dofusClassId = item.dofusClassId
            val dofusClass = BreedAssetManager.getAssets(dofusClassId)
            updateBgImg(dofusClass)
            updateIconImg(dofusClass)
        }
        updateBlurredImg(selected)
        pseudoLabel.text = item.pseudo
    }

    private fun updateBgImg(dofusBreedAssets: DofusBreedAssets) {
        bgImg = ImageUtil.getScaledImage(dofusBreedAssets.bannerData, (width * BANNER_WIDTH_RATIO).toInt())
    }

    private fun updateIconImg(dofusBreedAssets: DofusBreedAssets) {
        val iconImg =
            ImageUtil.getScaledImageKeepHeight(dofusBreedAssets.iconData, (height * ICON_HEIGHT_RATIO).toInt())
        iconLabel.icon = ImageIcon(iconImg)
    }

    private fun updateBlurredImg(selected: Boolean) {
        blurredImg = ImageUtil.blurImage(bgImg, getBlurRatio(selected))
        backgroundLabel.icon = ImageIcon(blurredImg)
    }

    private fun getBlurRatio(selected: Boolean): Float {
        return if (selected) FOCUS_BLUR_RATIO else BLUR_RATIO
    }

    override fun onScriptStart(character: DofusCharacter, script: DofusBotScript) {
        setStateBusy(script.name)
    }

    override fun onScriptEnd(character: DofusCharacter, endType: DofusBotScriptEndType) {
        setDefaultState()
    }

    override fun onListenStart() {
        setDefaultState()
    }

    override fun onListenStop() {
        setStateDisconnected()
    }

    fun updateState() {
        if (currentCharacterState != CharacterState.BUSY) {
            setDefaultState()
        }
    }

    private fun setDefaultState() {
        GameSnifferUtil.updateNetwork()
        val connection = GameSnifferUtil.getFirstConnection(character)
        if (connection != null) {
            if (GameSnifferUtil.getGameInfoByConnection(connection).shouldInitBoard) {
                setStateToInitialize()
            } else {
                setStateAvailable()
            }
        } else {
            setStateDisconnected()
        }
    }

    private fun setStateToInitialize() {
        currentCharacterState = CharacterState.TO_INITIALIZE
        activityLabel.toolTipText = "To initialize - switch to another map to initialize everything"
        updateActivityIcon(UiResource.ORANGE_CIRCLE)
    }

    private fun setStateAvailable() {
        currentCharacterState = CharacterState.AVAILABLE
        activityLabel.toolTipText = "Available"
        updateActivityIcon(UiResource.GREEN_CIRCLE)
    }

    private fun setStateBusy(scriptName: String) {
        currentCharacterState = CharacterState.BUSY
        activityLabel.toolTipText = "Script running : $scriptName"
        updateActivityIcon(UiResource.RED_CIRCLE)
    }

    private fun setStateDisconnected() {
        currentCharacterState = CharacterState.DISCONNECTED
        activityLabel.toolTipText = "Disconnected"
        updateActivityIcon(UiResource.BLACK_CIRCLE)
    }

    private enum class CharacterState {
        BUSY,
        AVAILABLE,
        TO_INITIALIZE,
        DISCONNECTED
    }

}