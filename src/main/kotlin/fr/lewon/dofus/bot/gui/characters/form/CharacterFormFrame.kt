package fr.lewon.dofus.bot.gui.characters.form

import fr.lewon.dofus.bot.gui.custom.CustomDialog
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import javax.swing.JFrame

class CharacterFormFrame(title: String, owner: JFrame, character: DofusCharacter = DofusCharacter()) : CustomDialog(
    title, 800, 400, AppColors.DEFAULT_UI_COLOR, 30, owner
) {

    val connectionPanel =
        CharacterFormConnectionPanel(size.width / 3, size.height - headerHeight - 30, character)
    val aiPanel =
        CharacterFormAiPanel(size.width * 2 / 3, size.height - headerHeight - 30, character)
    private val footer = CharacterFormFooter(this, size.width / 3, 80)
    var resultOk = false

    init {
        isAlwaysOnTop = ConfigManager.config.alwaysOnTop
        isModal = true
        connectionPanel.setBounds(0, headerHeight, size.width / 3, size.height - headerHeight)
        aiPanel.setBounds(size.width / 3, headerHeight, size.width * 2 / 3, size.height - headerHeight)
        footer.setBounds(0, size.height - 80, size.width / 3, 80)
        contentPane.add(footer)
        contentPane.add(connectionPanel)
        contentPane.add(aiPanel)
    }

}