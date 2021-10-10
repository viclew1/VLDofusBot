package fr.lewon.dofus.bot.gui.characters

import fr.lewon.dofus.bot.gui.MainFrame
import fr.lewon.dofus.bot.gui.characters.card.CharacterCard
import fr.lewon.dofus.bot.gui.characters.card.CharacterCardList
import fr.lewon.dofus.bot.gui.characters.form.CharacterFormFrame
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Font
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities


object CharactersTab : JPanel(MigLayout()) {

    private val addCharacterButton = JButton("+")

    private lateinit var charactersCardList: CharacterCardList

    init {
        addCharacterButton.font = Font("Tahoma", Font.BOLD, 20)
        addCharacterButton.isBorderPainted = false
        addCharacterButton.border = null
        addCharacterButton.margin = Insets(0, 0, 0, 0)
        addCharacterButton.isContentAreaFilled = false
        addCharacterButton.addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                addCharacterButton.foreground = Color.BLUE
            }

            override fun mouseExited(e: MouseEvent) {
                addCharacterButton.foreground = null
            }
        })
        add(addCharacterButton, "span 2 1, al right, wrap")
        addCharacterButton.addActionListener { processAddCharacterButton() }
        SwingUtilities.invokeLater { initAll() }
    }

    private fun initAll() {
        charactersCardList = CharacterCardList()
        val scrollPane = JScrollPane(charactersCardList)
        scrollPane.verticalScrollBar.unitIncrement *= 5
        scrollPane.horizontalScrollBar = null
        add(scrollPane, "width max, height max")
    }

    private fun processAddCharacterButton() {
        val form = CharacterFormFrame("Create character", MainFrame)
        form.isUndecorated = true
        form.setLocationRelativeTo(MainFrame)
        form.isVisible = true
        form.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent) {
                if (form.resultOk) {
                    val createdCharacter = CharacterManager.addCharacter(
                        form.connectionPanel.getLogin(),
                        form.connectionPanel.getPassword(),
                        form.connectionPanel.getPseudo(),
                        form.connectionPanel.getDofusClass(),
                        form.aiPanel.getSpells()
                    )
                    charactersCardList.addCharacterCard(createdCharacter)
                }
            }
        })
    }

    fun deleteCharacter(characterCard: CharacterCard) {
        charactersCardList.removeCharacter(characterCard)
    }

    fun updateCharacter(characterCard: CharacterCard) {
        val form = CharacterFormFrame("Update : ${characterCard.character.pseudo}", MainFrame, characterCard.character)
        form.isUndecorated = true
        form.setLocationRelativeTo(MainFrame)
        form.isVisible = true
        form.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent) {
                if (form.resultOk) {
                    CharacterManager.updateCharacter(
                        characterCard.character,
                        form.connectionPanel.getLogin(),
                        form.connectionPanel.getPassword(),
                        form.connectionPanel.getPseudo(),
                        form.connectionPanel.getDofusClass(),
                        form.aiPanel.getSpells()
                    )
                    characterCard.update(characterCard.character == CharacterManager.getCurrentCharacter())
                }
            }
        })
    }

}