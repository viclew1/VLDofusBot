package fr.lewon.dofus.bot.gui.characters.form

import fr.lewon.dofus.bot.gui.custom.CustomFrame
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.DofusClass
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import javax.swing.*

class CharacterFormFrame(title: String, character: DofusCharacter = DofusCharacter()) : CustomFrame(
    title,
    250,
    400,
    AppColors.DEFAULT_UI_COLOR,
    30,
    reduceButton = false
) {

    private val footerHeight = 80
    val connectionTab =
        CharacterFormConnectionTab(this, size.width, size.height - headerHeight - 30 - footerHeight, character)
    val aiTab =
        CharacterFormAiTab(this, size.width, size.height - headerHeight - 30 - footerHeight, character)
    private val footer = CharacterFormFooter(this, size.width, footerHeight)
    var resultOk = false
    private val backgroundLabel = JLabel()

    init {
        isAlwaysOnTop = ConfigManager.config.alwaysOnTop
        defaultCloseOperation = EXIT_ON_CLOSE
        backgroundLabel.verticalAlignment = SwingConstants.TOP
        val tabs = JTabbedPane()
        tabs.setBounds(0, headerHeight, size.width, size.height - headerHeight - footerHeight)
        tabs.addTab("Connection", connectionTab)
        tabs.addTab("Fight AI", aiTab)
        footer.setBounds(0, size.height - footerHeight, size.width, footerHeight)
        contentPane.add(tabs)
        contentPane.add(footer)
        contentPane.add(backgroundLabel)
    }

    fun updateBackground(dofusClass: DofusClass) {
        SwingUtilities.invokeLater {
            val tabHeight = 32
            val bgImg = ImageUtil.getScaledImageKeepHeight(dofusClass.bannerUrl, height - headerHeight - tabHeight)
            val blurredImg = ImageUtil.blurImage(bgImg, 0.9f)
            backgroundLabel.icon = ImageIcon(blurredImg)
            backgroundLabel.setBounds(
                width / 2 - bgImg.width / 2,
                headerHeight + tabHeight,
                bgImg.width,
                height - headerHeight
            )
        }
    }

}