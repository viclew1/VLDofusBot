package fr.lewon.dofus.bot.gui.panes.status

import fr.lewon.dofus.bot.gui.util.AppColors
import net.miginfocom.swing.MigLayout
import org.jdesktop.swingx.JXBusyLabel
import javax.swing.JLabel
import javax.swing.JPanel

object StatusPanel : JPanel(MigLayout("insets 0")) {

    private val progressCircle = JXBusyLabel()
    private val statusLabel = JLabel()

    init {
        progressCircle.isVisible = false
        background = AppColors.DEFAULT_UI_COLOR
        add(progressCircle, "h max")
        add(statusLabel, "h max")
    }

    fun changeText(text: String) {
        statusLabel.text = text
    }

}