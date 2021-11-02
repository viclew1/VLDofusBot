package fr.lewon.dofus.bot.gui.tabs.characters.form

import fr.lewon.dofus.bot.gui.custom.OutlineJLabel
import fr.lewon.dofus.bot.gui.tabs.characters.dofusclass.DofusClassComboBox
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.model.characters.DofusClass
import java.awt.Color
import java.awt.Font
import javax.swing.*

class CharacterFormConnectionPanel(w: Int, h: Int, character: DofusCharacter = DofusCharacter()) : JPanel() {

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
    private val backgroundLabel = JLabel()

    init {
        layout = null
        setSize(w, h)
        classComboBox.selectedItem = character.dofusClass
        classComboBox.addItemListener { updateBackground() }

        val labelsInputsPairs = listOf(
            Pair(loginLabel, loginTextField),
            Pair(passwordLabel, passwordTextField),
            Pair(pseudoLabel, pseudoTextField),
            Pair(classLabel, classComboBox)
        )

        val dx = (LABEL_DELTA_WIDTH_RATIO * width).toInt()
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

        add(backgroundLabel)
        backgroundLabel.verticalAlignment = SwingConstants.TOP
        updateBackground()
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

    fun getLogin(): String {
        return loginTextField.text
    }

    fun getPassword(): String {
        return String(passwordTextField.password)
    }

    fun getPseudo(): String {
        return pseudoTextField.text
    }

    fun getDofusClass(): DofusClass {
        return classComboBox.selectedItem as DofusClass
    }

}