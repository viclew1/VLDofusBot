package fr.lewon.dofus.bot.gui.characters.form

import fr.lewon.dofus.bot.game.classes.DofusClass
import fr.lewon.dofus.bot.gui.characters.dofusclass.DofusClassComboBox
import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import java.awt.Color
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextField

class CharacterFormConnectionTab(
    private val characterFormFrame: CharacterFormFrame,
    w: Int,
    h: Int,
    character: DofusCharacter = DofusCharacter()
) : JPanel() {

    companion object {
        private const val LABEL_DELTA_WIDTH_RATIO = 1f / 20f
        private const val LABEL_HEIGHT = 40
        private const val TEXT_FIELD_HEIGHT = 25
        private const val TEXT_FIELD_DELTA_HEIGHT = (LABEL_HEIGHT - TEXT_FIELD_HEIGHT) / 2
    }

    private val loginLabel = OutlineJLabel("Login")
    private val loginTextField = JTextField(character.login)
    private val passwordLabel = OutlineJLabel("Password")
    private val passwordTextField = JPasswordField(character.password)
    private val pseudoLabel = OutlineJLabel("Pseudo")
    private val pseudoTextField = JTextField(character.pseudo)
    private val classLabel = OutlineJLabel("Class")
    private val classComboBox = DofusClassComboBox()

    init {
        layout = null
        setSize(w, h)
        isOpaque = false
        classComboBox.selectedItem = character.dofusClass
        classComboBox.addItemListener { updateBackground() }

        loginLabel.foreground = Color.WHITE
        passwordLabel.foreground = Color.WHITE
        pseudoLabel.foreground = Color.WHITE
        classLabel.foreground = Color.WHITE

        val dx = (LABEL_DELTA_WIDTH_RATIO * width).toInt()
        val baseY = height / 4
        val labelWidth = width / 2 - dx
        loginLabel.setBounds(dx, baseY, labelWidth, LABEL_HEIGHT)
        passwordLabel.setBounds(dx, baseY + LABEL_HEIGHT, labelWidth, LABEL_HEIGHT)
        pseudoLabel.setBounds(dx, baseY + LABEL_HEIGHT * 2, labelWidth, LABEL_HEIGHT)
        classLabel.setBounds(dx, baseY + LABEL_HEIGHT * 3, labelWidth, LABEL_HEIGHT)

        loginTextField.setBounds(width / 2, baseY + TEXT_FIELD_DELTA_HEIGHT, labelWidth, TEXT_FIELD_HEIGHT)
        passwordTextField.setBounds(
            width / 2,
            baseY + TEXT_FIELD_DELTA_HEIGHT + LABEL_HEIGHT,
            labelWidth,
            TEXT_FIELD_HEIGHT
        )
        pseudoTextField.setBounds(
            width / 2,
            baseY + TEXT_FIELD_DELTA_HEIGHT + LABEL_HEIGHT * 2,
            labelWidth,
            TEXT_FIELD_HEIGHT
        )
        classComboBox.setBounds(
            width / 2,
            baseY + TEXT_FIELD_DELTA_HEIGHT + LABEL_HEIGHT * 3,
            labelWidth,
            TEXT_FIELD_HEIGHT
        )

        val font = Font("Impact", Font.PLAIN, 20)
        loginLabel.font = font
        passwordLabel.font = font
        pseudoLabel.font = font
        classLabel.font = font

        add(loginLabel)
        add(loginTextField)
        add(passwordLabel)
        add(passwordTextField)
        add(pseudoLabel)
        add(pseudoTextField)
        add(classLabel)
        add(classComboBox)
        updateBackground()
    }

    private fun updateBackground() {
        characterFormFrame.updateBackground(classComboBox.selectedItem as DofusClass)
    }

    fun getLogin(): String {
        return loginTextField.text
    }

    fun getPassword(): String {
        return passwordTextField.text
    }

    fun getPseudo(): String {
        return pseudoTextField.text
    }

    fun getDofusClass(): DofusClass {
        return classComboBox.selectedItem as DofusClass
    }

}