package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.custom.CustomJTextField
import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameterType
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import fr.lewon.dofus.bot.util.script.DofusBotScriptEndType
import fr.lewon.dofus.bot.util.script.ScriptRunner
import fr.lewon.dofus.bot.util.script.ScriptRunnerListener
import net.miginfocom.swing.MigLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.border.EmptyBorder

class CharacterScriptPanel(private val character: DofusCharacter) : JPanel(MigLayout()), ScriptRunnerListener {

    companion object {
        private const val BUTTON_SZ = 36
    }

    private val scriptComboBox = DofusScriptComboBox()
    private val descriptionTextArea = JTextArea()

    private val parametersSeparator = JSeparator(JSeparator.HORIZONTAL)
    private val parametersLabel = JLabel("Parameters").also { it.font = AppFonts.TITLE_FONT }
    private val parametersScrollPane = JScrollPane()

    private val statsSeparator = JSeparator(JSeparator.HORIZONTAL).also { it.isVisible = false }
    private val statsLabel = JLabel("Stats").also { it.font = AppFonts.TITLE_FONT }.also { it.isVisible = false }
    private val statsPanel = StatsPanel().also { it.isVisible = false }

    private val startStopScriptButton = JButton().also {
        it.isBorderPainted = false
        it.border = null
        it.margin = Insets(0, 0, 0, 0)
        it.isContentAreaFilled = false
        it.addActionListener { toggleScript() }
        ScriptRunner.addListener(character, this)
    }

    private var scriptRunning = false

    init {
        updateStartStopButton(UiResource.PLAY_ARROW)
        addScriptLauncherPane()
        addDescriptionPane()
        addParametersPane()
        addStatsPane()
    }

    private fun updateStartStopButton(uiResource: UiResource) {
        val imageData = uiResource.imageData
        val filledImageData = uiResource.filledImageData
        startStopScriptButton.icon = ImageIcon(ImageUtil.getScaledImage(imageData, BUTTON_SZ, BUTTON_SZ))
        startStopScriptButton.rolloverIcon =
            ImageIcon(ImageUtil.getScaledImage(filledImageData, BUTTON_SZ, BUTTON_SZ))
        startStopScriptButton.isEnabled = true
    }

    private fun addScriptLauncherPane() {
        scriptComboBox.addItemListener { updateScript() }
        updateScript()
        add(JLabel("Script").also { it.font = AppFonts.TITLE_FONT })
        add(scriptComboBox, "al right")
        add(startStopScriptButton, "gap 50, al left, wrap")
    }

    private fun addDescriptionPane() {
        descriptionTextArea.wrapStyleWord = true
        descriptionTextArea.lineWrap = true
        descriptionTextArea.isEditable = false
        descriptionTextArea.isFocusable = false
        descriptionTextArea.isOpaque = false
        descriptionTextArea.border = EmptyBorder(5, 5, 5, 5)
        add(JSeparator(JSeparator.HORIZONTAL), "span 3 1, width max, wrap")
        add(JLabel("Description").also { it.font = AppFonts.TITLE_FONT }, "wrap")
        val descriptionScrollPane = JScrollPane(descriptionTextArea)
        descriptionScrollPane.horizontalScrollBar = null
        add(descriptionScrollPane, "span 3 1, width max, height 120:120:120, wrap")
    }

    private fun addParametersPane() {
        add(parametersSeparator, "span 3 1, width max, wrap")
        add(parametersLabel, "wrap")
        parametersScrollPane.horizontalScrollBar = null
        add(parametersScrollPane, "span 3 1, h 120:120:120, width max, wrap")
    }

    private fun addStatsPane() {
        add(statsSeparator, "span 3 1, width max, wrap")
        add(statsLabel, "wrap")
        add(statsPanel, "span 3 1, width max, wrap")
    }

    private fun buildParametersPane(): JPanel {
        val dofusScript = scriptComboBox.selectedItem as DofusBotScript
        val parametersPane = JPanel(MigLayout("fillX"))
        val parameters = dofusScript.getParameters()
        parametersSeparator.isVisible = parameters.isNotEmpty()
        parametersLabel.isVisible = parameters.isNotEmpty()
        parametersScrollPane.isVisible = parameters.isNotEmpty()
        for (parameter in parameters) {
            val parameterLabel = JLabel(parameter.key).also { it.toolTipText = parameter.description }
            parametersPane.add(parameterLabel, "height 20")
            parametersPane.add(JPanel(), "growX, height 20")
            parametersPane.add(
                buildInputField(character, dofusScript, parameter),
                "al right, width 80, height 25, wrap"
            )
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
        integerField.addCaretListener { updateParam(character, script, param, integerField.text.toIntOrNull() ?: 0) }
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
        val comboBox = JComboBox(param.possibleValues.toTypedArray())
        comboBox.selectedItem = param.value
        comboBox.addItemListener { updateParam(character, script, param, comboBox.selectedItem?.toString() ?: "") }
        return comboBox.also { it.addItemListener { } }
    }

    private fun buildTextField(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter
    ): JComponent {
        val textField = CustomJTextField(param.value)
        textField.addCaretListener { updateParam(character, script, param, textField.text) }
        return textField
    }

    private fun updateParam(
        character: DofusCharacter,
        script: DofusBotScript,
        param: DofusBotScriptParameter,
        value: Any
    ) {
        param.value = value.toString()
        CharacterManager.updateParamValue(character, script, param)
        updateDescription()
    }

    private fun updateScript() {
        parametersScrollPane.setViewportView(buildParametersPane())
        updateDescription()
        super.updateUI()
    }

    private fun updateDescription() {
        val dofusScript = scriptComboBox.selectedItem as DofusBotScript
        descriptionTextArea.text = dofusScript.getDescription()
    }

    private fun toggleScript() {
        startStopScriptButton.isEnabled = false
        if (scriptRunning) {
            stopScript()
        } else {
            startScript()
        }
    }

    private fun startScript() {
        scriptRunning = true
        statsSeparator.isVisible = true
        statsLabel.isVisible = true
        statsPanel.isVisible = true
        val script = scriptComboBox.selectedItem as DofusBotScript
        ScriptRunner.runScript(character, script)
    }

    private fun stopScript() {
        ScriptRunner.stopScript(character)
    }

    override fun onScriptEnd(endType: DofusBotScriptEndType) {
        updateStartStopButton(UiResource.PLAY_ARROW)
        statsPanel.stopStatsUpdate()
        scriptRunning = false
    }

    override fun onScriptStart(script: DofusBotScript) {
        statsPanel.startStatsUpdate(script)
        updateStartStopButton(UiResource.STOP)
    }

}