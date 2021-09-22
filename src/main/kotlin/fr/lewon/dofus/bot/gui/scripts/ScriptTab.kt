package fr.lewon.dofus.bot.gui.scripts

import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.ui.ScriptRunner
import net.miginfocom.swing.MigLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.border.EmptyBorder


object ScriptTab : JPanel(MigLayout()) {

    private val scriptComboBox = DofusScriptComboBox()
    private val descriptionTextArea = JTextArea()

    private val parametersSeparator = JSeparator(JSeparator.HORIZONTAL)
    private val parametersLabel = JLabel("Parameters")
    private val parametersScrollPane = JScrollPane()
    private val startScriptButton = JButton()

    init {
        descriptionTextArea.wrapStyleWord = true
        descriptionTextArea.lineWrap = true
        descriptionTextArea.isEditable = false
        descriptionTextArea.isFocusable = false
        descriptionTextArea.isOpaque = false
        descriptionTextArea.border = EmptyBorder(5, 5, 5, 5)
        scriptComboBox.addItemListener { updateScript() }
        updateScript()
        add(JLabel("Script"))
        add(scriptComboBox, "al right")
        val imageUrl = javaClass.getResource("/icon/ui/play_arrow.png")
        val imageFilledUrl = javaClass.getResource("/icon/ui/play_arrow_filled.png")
        startScriptButton.isBorderPainted = false
        startScriptButton.border = null
        startScriptButton.margin = Insets(0, 0, 0, 0)
        startScriptButton.isContentAreaFilled = false
        startScriptButton.icon = ImageIcon(ImageUtil.getScaledImage(imageUrl, 36, 36))
        startScriptButton.rolloverIcon = ImageIcon(ImageUtil.getScaledImage(imageFilledUrl, 36, 36))
        startScriptButton.addActionListener { startScript() }
        add(startScriptButton, "al center, wrap")
        add(JSeparator(JSeparator.HORIZONTAL), "span 3 1, width max, wrap")
        add(JLabel("Description"), "wrap")
        val descriptionScrollPane = JScrollPane(descriptionTextArea)
        descriptionScrollPane.horizontalScrollBar = null
        add(descriptionScrollPane, "span 3 1, width max, height 100, wrap")
        add(parametersSeparator, "span 3 1, width max, wrap")
        add(parametersLabel, "wrap")
        parametersScrollPane.horizontalScrollBar = null
        add(parametersScrollPane, "span 3 1, width max, wrap")
    }

    private fun buildParametersPane(): JPanel {
        val dofusScript = scriptComboBox.selectedItem as DofusBotScript
        val parametersPane = JPanel(MigLayout("fillx"))
        val character = CharacterManager.getCurrentCharacter()
        val parameters = dofusScript.getParameters()
        parametersSeparator.isVisible = parameters.isNotEmpty()
        parametersLabel.isVisible = parameters.isNotEmpty()
        parametersScrollPane.isVisible = parameters.isNotEmpty()
        if (character != null) {
            for (parameter in parameters) {
                parametersPane.add(JLabel(parameter.key), "height 20")
                parametersPane.add(JPanel(), "growx, height 20")
                parametersPane.add(
                    buildInputField(character, dofusScript, parameter),
                    "al right, width 80, height 25, wrap"
                )
            }
        }
        return parametersPane
    }

    private fun buildInputField(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter
    ): JComponent {
        param.value = CharacterManager.getParamValue(character, script, param) ?: param.defaultValue
        return when (param.type) {
            DofusBotScriptParameterType.INTEGER -> buildIntegerField(character, script, param)
            DofusBotScriptParameterType.BOOLEAN -> buildCheckbox(character, script, param)
            DofusBotScriptParameterType.CHOICE -> buildComboBox(character, script, param)
            else -> buildTextField(character, script, param)
        }
    }

    private fun buildIntegerField(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter
    ): JComponent {
        val integerField = IntegerJTextField(param.value.toInt())
        integerField.addCaretListener { updateParam(character, script, param, integerField.text) }
        return integerField
    }

    private fun buildCheckbox(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter
    ): JComponent {
        val checkBox = JCheckBox()
        checkBox.isSelected = param.value.toBoolean()
        checkBox.addItemListener { updateParam(character, script, param, checkBox.isSelected.toString()) }
        return checkBox
    }

    private fun buildComboBox(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter
    ): JComponent {
        val comboBox = JComboBox<String>(param.possibleValues.toTypedArray())
        comboBox.selectedItem = param.value
        comboBox.addItemListener { updateParam(character, script, param, comboBox.selectedItem?.toString() ?: "") }
        return comboBox.also { it.addItemListener { } }
    }

    private fun buildTextField(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter
    ): JComponent {
        val textField = JTextField(param.value)
        textField.addCaretListener { updateParam(character, script, param, textField.text) }
        return textField
    }

    private fun updateParam(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter,
        value: String
    ) {
        param.value = value
        CharacterManager.updateParamValue(character, script, param)
        updateDescription()
    }

    fun updateScript() {
        parametersScrollPane.setViewportView(buildParametersPane())
        updateDescription()
        super.updateUI()
    }

    private fun updateDescription() {
        val dofusScript = scriptComboBox.selectedItem as DofusBotScript
        descriptionTextArea.text = dofusScript.getDescription()
    }

    private fun startScript() {
        val dofusScript = scriptComboBox.selectedItem as DofusBotScript
        ScriptRunner.runScript(dofusScript)
    }

}