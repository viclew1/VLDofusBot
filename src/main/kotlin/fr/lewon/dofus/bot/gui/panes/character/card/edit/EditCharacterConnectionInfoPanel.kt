package fr.lewon.dofus.bot.gui.panes.character.card.edit

import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.gui.custom.listrenderer.TextImageComboBox
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.DofusClass
import fr.lewon.dofus.bot.model.characters.spells.SpellCombination
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.filemanagers.DofusClassManager
import java.awt.Color
import java.awt.Font
import javax.swing.*

class EditCharacterConnectionInfoPanel(
    character: DofusCharacter,
    spells: List<SpellCombination>,
    onSaveAction: (DofusCharacter) -> Unit
) : JPanel() {

    companion object {
        private const val SAVE_BUTTON_W_WIDTH_RATIO = 3f / 10f
        private const val LABEL_DELTA_WIDTH_RATIO = 1f / 20f
        private const val ERRORS_AREA_DELTA_WIDTH_RATIO = 1f / 30f
        private const val LABEL_HEIGHT = 40
        private const val TEXT_FIELD_HEIGHT = 25
        private const val TEXT_FIELD_DELTA_HEIGHT = (LABEL_HEIGHT - TEXT_FIELD_HEIGHT) / 2
    }

    private val loginLabel = OutlineJLabel("Login")
    private val loginTextField = JTextField(character.login).also {
        it.addCaretListener { updateSaveButton() }
    }
    private val passwordLabel = OutlineJLabel("Password")
    private val passwordTextField = JPasswordField(character.password).also {
        it.addCaretListener { updateSaveButton() }
    }
    private val pseudoLabel = OutlineJLabel("Pseudo")
    private val pseudoTextField = JTextField(character.pseudo).also {
        it.addCaretListener { updateSaveButton() }
    }
    private val classLabel = OutlineJLabel("Class")
    private val classComboBox = TextImageComboBox(25, DofusClassManager.getAllClasses().toTypedArray()).also {
        it.addItemListener { updateSaveButton() }
    }
    private val backgroundLabel = JLabel()
    private val saveButton = JButton("Save")
    private val errorArea = JTextArea().also {
        it.lineWrap = true
        it.font = AppFonts.ERROR_FONT
        it.disabledTextColor = Color.RED
        it.isEnabled = false
        it.isOpaque = false
    }

    init {
        layout = null
        setSize(GlobalCharacterFormPanel.CONNECTION_INFO_WIDTH, GlobalCharacterFormPanel.CONNECTION_INFO_HEIGHT)
        classComboBox.selectedItem = DofusClassManager.getClass(character.dofusClassId)
        classComboBox.addItemListener { updateBackground() }
        saveButton.addActionListener { saveCharacter(character, spells, onSaveAction) }

        val labelsInputsPairs = listOf(
            Pair(loginLabel, loginTextField),
            Pair(passwordLabel, passwordTextField),
            Pair(pseudoLabel, pseudoTextField),
            Pair(classLabel, classComboBox)
        )

        val dx = (LABEL_DELTA_WIDTH_RATIO * width).toInt()
        val buttonWidth = (SAVE_BUTTON_W_WIDTH_RATIO * width).toInt()
        saveButton.setBounds(dx, dx, buttonWidth, 40)
        add(saveButton)

        val labelBaseY = height / 4
        val componentWidth = width / 2 - dx
        val inputBaseY = labelBaseY + TEXT_FIELD_DELTA_HEIGHT
        val font = Font("Impact", Font.PLAIN, 20)

        for (i in labelsInputsPairs.indices) {
            val label = labelsInputsPairs[i].first
            val input = labelsInputsPairs[i].second

            label.foreground = Color.WHITE
            label.font = font
            label.setBounds(dx, labelBaseY + i * LABEL_HEIGHT, componentWidth, LABEL_HEIGHT)
            input.setBounds(width / 2, inputBaseY + LABEL_HEIGHT * i, componentWidth, TEXT_FIELD_HEIGHT)

            add(label)
            add(input)
        }

        val errorAreaDx = (ERRORS_AREA_DELTA_WIDTH_RATIO * width).toInt()
        val errorAreaWidth = width - 2 * errorAreaDx
        val errorAreaY = labelBaseY + labelsInputsPairs.size * LABEL_HEIGHT
        val errorAreaHeight = height - errorAreaY - errorAreaDx
        errorArea.setBounds(errorAreaDx, errorAreaY, errorAreaWidth, errorAreaHeight)
        errorArea.setSize(errorAreaWidth, errorAreaHeight)
        add(errorArea)

        add(backgroundLabel)
        backgroundLabel.verticalAlignment = SwingConstants.TOP
        updateBackground()
        updateSaveButton()
    }

    private fun saveCharacter(
        character: DofusCharacter, spells: List<SpellCombination>, onSaveAction: (DofusCharacter) -> Unit
    ) {
        errorArea.text = ""
        errorArea.isOpaque = false
        val existingCharacter = CharacterManager.getCharacter(getLogin(), getPseudo())
        if (getLogin().isBlank()) {
            errorArea.text += "- Missing param : login\n"
        }
        if (getPassword().isBlank()) {
            errorArea.text += "- Missing param : password\n"
        }
        if (getPseudo().isBlank()) {
            errorArea.text += "- Missing param : pseudo\n "
        }
        if (existingCharacter != null && existingCharacter !== character) {
            errorArea.text = "- Non unique login / pseudo combination\n"
        }
        if (errorArea.text.isEmpty()) {
            character.pseudo = getPseudo()
            character.password = getPassword()
            character.login = getLogin()
            character.dofusClassId = getDofusClass().breed.id
            character.spells = ArrayList(spells)
            onSaveAction(character)
        } else {
            errorArea.isOpaque = true
        }
    }

    private fun updateSaveButton() {
        saveButton.isEnabled = getLogin().isNotBlank()
                && getPassword().isNotBlank()
                && getPseudo().isNotBlank()
    }

    private fun updateBackground() {
        SwingUtilities.invokeLater {
            val dofusClass = classComboBox.selectedItem as DofusClass
            val bgImg = ImageUtil.getScaledImageKeepHeight(dofusClass.bannerData, height)
            val blurredImg = ImageUtil.blurImage(bgImg, 0.85f)
            backgroundLabel.icon = ImageIcon(blurredImg)
            backgroundLabel.setBounds(width / 2 - bgImg.width / 2, 0, bgImg.width, height)
            parent.repaint()
        }
    }

    private fun getLogin(): String {
        return loginTextField.text
    }

    private fun getPassword(): String {
        return String(passwordTextField.password)
    }

    private fun getPseudo(): String {
        return pseudoTextField.text
    }

    private fun getDofusClass(): DofusClass {
        return classComboBox.selectedItem as DofusClass
    }

}