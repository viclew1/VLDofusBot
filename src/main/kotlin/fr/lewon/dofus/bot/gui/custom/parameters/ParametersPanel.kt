package fr.lewon.dofus.bot.gui.custom.parameters

import fr.lewon.dofus.bot.gui.custom.CustomJTextField
import fr.lewon.dofus.bot.gui.custom.IntegerJTextField
import fr.lewon.dofus.bot.scripts.DofusBotParameter
import fr.lewon.dofus.bot.scripts.DofusBotParameterType
import net.miginfocom.swing.MigLayout
import javax.swing.*

class ParametersPanel(
    parameters: List<DofusBotParameter>,
    private val onParamUpdate: (DofusBotParameter, Any) -> Unit
) : JPanel(MigLayout("fillX")) {

    init {
        for (parameter in parameters) {
            val parameterLabel = JLabel(parameter.key).also { it.toolTipText = parameter.description }
            add(parameterLabel, "height 20")
            add(JPanel(), "growX, height 20")
            add(buildInputField(parameter), "al right, width 100:100:100, height 25, wrap")
        }
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
        return comboBox.also { it.addItemListener { } }
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
        onParamUpdate(param, value)
    }
}