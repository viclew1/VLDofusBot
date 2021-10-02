package fr.lewon.dofus.bot.gui.characters.form

import java.awt.Color
import java.awt.Font
import javax.swing.JButton
import javax.swing.JPanel

class CharacterFormFooter(private val characterFormFrame: CharacterFormFrame, w: Int, h: Int) : JPanel() {

    private val okButton = JButton("OK")

    init {
        layout = null
        setSize(w, h)
        isOpaque = false

        val okButtonHeight = 40
        val okButtonWidth = 60
        okButton.setBounds(
            width / 2 - okButtonWidth / 2,
            height / 2 - okButtonHeight / 2,
            okButtonWidth,
            okButtonHeight
        )

        okButton.addActionListener {
            characterFormFrame.resultOk = true
            characterFormFrame.dispose()
        }

        val font = Font("Impact", Font.PLAIN, 20)
        okButton.font = font
        okButton.foreground = Color.WHITE

        updateOkButtonEnable()

        add(okButton)
    }

    fun updateOkButtonEnable() {
        if (characterFormFrame.connectionPanel.getLogin().isEmpty()) {
            okButton.isEnabled = false
        }
        if (characterFormFrame.connectionPanel.getPassword().isEmpty()) {
            okButton.isEnabled = false
        }
        if (characterFormFrame.connectionPanel.getPseudo().isEmpty()) {
            okButton.isEnabled = false
        }
        okButton.isEnabled = true
    }

}