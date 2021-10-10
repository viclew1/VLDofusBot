package fr.lewon.dofus.bot.gui.about

import net.miginfocom.swing.MigLayout
import java.awt.Desktop
import java.net.URI
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

object AboutTab : JPanel(MigLayout()) {

    private const val githubLink = "https://github.com/viclew1/VLDofusBot"
    private const val message =
        "<html>Thank you for using VL Dofus Bot, feel free to visit the project github page for issues, suggestions or just to give it a star ! <br>" +
            "Link to the project :"

    private val githubButton = JButton(githubLink)

    init {
        add(JLabel(message, JLabel.CENTER), "width max, wrap")
        add(githubButton, "width max")
        githubButton.addActionListener {
            val desktop = Desktop.getDesktop()
            val url = URI(githubLink)
            desktop.browse(url)
        }
    }

}