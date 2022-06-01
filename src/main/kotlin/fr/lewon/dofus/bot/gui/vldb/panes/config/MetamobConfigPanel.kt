package fr.lewon.dofus.bot.gui.vldb.panes.config

import fr.lewon.dofus.bot.util.filemanagers.impl.MetamobConfigManager
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JTextField

object MetamobConfigPanel : AbstractConfigPanel() {

    private val metamobUniqueIdLabel = JLabel("Metamob unique ID")
    private val metamobUniqueIdTextField = JTextField()
    private val metamobPseudoLabel = JLabel("Metamob pseudo")
    private val metamobPseudoTextField = JTextField()
    private val tradeAutoUpdateLabel = JLabel("Auto update on trade")
    private val tradeAutoUpdateCheckbox = JCheckBox()
    private val captureAutoUpdateLabel = JLabel("Auto update on capture")
    private val captureAutoUpdateCheckbox = JCheckBox()

    init {
        // Metamob unique identifier
        metamobUniqueIdTextField.text = MetamobConfigManager.readConfig().metamobUniqueID
        metamobUniqueIdLabel.toolTipText =
            "Needed to use Metamob Helper, you can find it on Metamob under profile / API"
        addLine(metamobUniqueIdLabel, metamobUniqueIdTextField)
        metamobUniqueIdTextField.addCaretListener {
            updateMetamobUniqueID(metamobUniqueIdTextField.text)
        }

        // Metamob pseudo
        metamobPseudoTextField.text = MetamobConfigManager.readConfig().metamobPseudo
        metamobPseudoLabel.toolTipText = "Needed to use Metamob Helper, your Metamob pseudo"
        addLine(metamobPseudoLabel, metamobPseudoTextField)
        metamobPseudoTextField.addCaretListener {
            updateMetamobPseudo(metamobPseudoTextField.text)
        }

        // Auto update on trade
        tradeAutoUpdateCheckbox.isSelected = MetamobConfigManager.readConfig().tradeAutoUpdate
        tradeAutoUpdateLabel.toolTipText =
            "Auto updates metamob when an initialized character trades monsters with someone"
        addLine(tradeAutoUpdateLabel, tradeAutoUpdateCheckbox)
        tradeAutoUpdateCheckbox.addItemListener {
            updateTradeAutoUpdate(tradeAutoUpdateCheckbox.isSelected)
        }

        // Auto update on capture
        captureAutoUpdateCheckbox.isSelected = MetamobConfigManager.readConfig().captureAutoUpdate
        captureAutoUpdateLabel.toolTipText =
            "Auto updates metamob when an initialized character captures a monster group"
        addLine(captureAutoUpdateLabel, captureAutoUpdateCheckbox)
        captureAutoUpdateCheckbox.addItemListener {
            updateCaptureAutoUpdate(captureAutoUpdateCheckbox.isSelected)
        }
    }

    private fun updateMetamobUniqueID(metamobUniqueID: String) {
        MetamobConfigManager.editConfig { it.metamobUniqueID = metamobUniqueID }
    }

    private fun updateMetamobPseudo(metamobPseudo: String) {
        MetamobConfigManager.editConfig { it.metamobPseudo = metamobPseudo }
    }

    private fun updateTradeAutoUpdate(tradeAutoUpdate: Boolean) {
        MetamobConfigManager.editConfig { it.tradeAutoUpdate = tradeAutoUpdate }
    }

    private fun updateCaptureAutoUpdate(captureAutoUpdate: Boolean) {
        MetamobConfigManager.editConfig { it.captureAutoUpdate = captureAutoUpdate }
    }
}