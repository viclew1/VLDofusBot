package fr.lewon.dofus.bot.gui.custom.parameters

import fr.lewon.dofus.bot.gui.custom.CustomJTextField
import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameter
import fr.lewon.dofus.bot.scripts.parameters.DofusBotParameterType
import java.awt.Dimension
import javax.swing.*

class ParametersPanel(
    parameters: List<DofusBotParameter>,
    private val onParamUpdate: (DofusBotParameter, Any) -> Unit
) : JPanel() {

    private val boxByParameter = HashMap<DofusBotParameter, Box>()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(Box.createRigidArea(Dimension(5, 5)))
        for (parameter in parameters) {
            val parameterPanel = Box.createHorizontalBox()
            val parameterLabel = JLabel(parameter.key).also { it.toolTipText = parameter.description }
            parameterPanel.add(Box.createRigidArea(Dimension(5 + 10 * getParameterLevel(parameter), 5)))
            parameterPanel.add(parameterLabel)
            parameterPanel.add(Box.createHorizontalGlue())
            parameterPanel.add(buildInputField(parameter).also {
                it.maximumSize = Dimension(120, 25)
                it.preferredSize = it.maximumSize
            })
            parameterPanel.add(Box.createRigidArea(Dimension(10, 5)))
            parameterPanel.isVisible = parameter.displayCondition()
            add(parameterPanel)
            boxByParameter[parameter] = parameterPanel
        }
    }

    private fun getParameterLevel(parameter: DofusBotParameter): Int {
        val parent = parameter.parentParameter ?: return 0
        return 1 + getParameterLevel(parent)
    }

    private fun buildInputField(param: DofusBotParameter): JComponent {
        return when (param.type) {
            DofusBotParameterType.INTEGER -> buildIntegerField(param)
            DofusBotParameterType.BOOLEAN -> buildCheckbox(param)
            DofusBotParameterType.CHOICE -> buildComboBox(param)
            else -> buildTextField(param)
        }
    }

    private fun buildIntegerField(param: DofusBotParameter): JComponent {
        val integerField = IntegerJTextField(param.value.toInt())
        integerField.addCaretListener {
            updateParam(param, integerField.text.toIntOrNull() ?: 0)
        }
        return integerField
    }

    private fun buildCheckbox(param: DofusBotParameter): JComponent {
        val checkBox = JCheckBox()
        checkBox.isSelected = param.value.toBoolean()
        checkBox.addItemListener {
            updateParam(param, checkBox.isSelected.toString())
        }
        return checkBox
    }

    private fun buildComboBox(param: DofusBotParameter): JComponent {
        val comboBox = JComboBox(param.possibleValues.toTypedArray())
        comboBox.selectedItem = param.value
        comboBox.addItemListener {
            updateParam(param, comboBox.selectedItem?.toString() ?: "")
        }
        return comboBox
    }

    private fun buildTextField(param: DofusBotParameter): JComponent {
        val textField = CustomJTextField(param.value)
        textField.addCaretListener {
            updateParam(param, textField.text)
        }
        return textField
    }

    private fun updateParam(param: DofusBotParameter, value: Any) {
        param.value = value.toString()
        boxByParameter.entries.forEach { updateDisplay(it.value, it.key) }
        onParamUpdate(param, value)
    }

    private fun updateDisplay(inputField: JComponent, parameter: DofusBotParameter) {
        inputField.isVisible = parameter.displayCondition()
    }
}