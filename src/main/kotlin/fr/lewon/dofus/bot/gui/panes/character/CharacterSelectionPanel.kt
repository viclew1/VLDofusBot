package fr.lewon.dofus.bot.gui.panes.character

import fr.lewon.dofus.bot.gui.MainPanel
import fr.lewon.dofus.bot.gui.VldbMainFrame
import fr.lewon.dofus.bot.gui.panes.character.card.CharacterCard
import fr.lewon.dofus.bot.gui.panes.character.card.CharacterCardList
import fr.lewon.dofus.bot.gui.panes.character.form.CharacterFormFrame
import fr.lewon.dofus.bot.gui.util.AppFonts
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

object CharacterSelectionPanel : JPanel(MigLayout("insets 0, fill")) {

    private val addCharacterButton = JButton("+")

    private lateinit var charactersCardList: CharacterCardList

    init {
        val titleLb = JLabel("Characters")
        titleLb.font = AppFonts.TITLE_FONT
        add(titleLb, "pad 0 5, alignY center")

        addCharacterButton.font = AppFonts.HEADER_FONT
        addCharacterButton.isBorderPainted = false
        addCharacterButton.border = null
        addCharacterButton.margin = Insets(0, 0, 0, 0)
        addCharacterButton.isContentAreaFilled = false
        addCharacterButton.addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                addCharacterButton.foreground = Color.BLACK
            }

            override fun mouseExited(e: MouseEvent) {
                addCharacterButton.foreground = null
            }
        })
        add(addCharacterButton, "al right, wrap")
        addCharacterButton.addActionListener { processAddCharacterButton() }
        initAll()
    }

    private fun initAll() {
        charactersCardList = CharacterCardList(MainPanel.CHARACTERS_WIDTH)
        val scrollPane = JScrollPane(charactersCardList)
        scrollPane.verticalScrollBar.unitIncrement *= 5
        scrollPane.horizontalScrollBar = null
        scrollPane.border = BorderFactory.createEmptyBorder()
        add(scrollPane, "span 2 1, width max, height max")
    }

    private fun processAddCharacterButton() {
        val form = CharacterFormFrame("Create character", VldbMainFrame)
        form.isUndecorated = true
        form.setLocationRelativeTo(VldbMainFrame)
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
        val form =
            CharacterFormFrame("Update : ${characterCard.character.pseudo}", VldbMainFrame, characterCard.character)
        form.isUndecorated = true
        form.setLocationRelativeTo(VldbMainFrame)
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