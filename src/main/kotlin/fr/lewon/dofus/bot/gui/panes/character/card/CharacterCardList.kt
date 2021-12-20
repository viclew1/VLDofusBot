package fr.lewon.dofus.bot.gui.panes.character.card

import fr.lewon.dofus.bot.gui.MainPanel
import fr.lewon.dofus.bot.gui.panes.character.card.listener.CharacterCardListener
import fr.lewon.dofus.bot.gui.panes.character.card.listener.DragManager
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import net.miginfocom.swing.MigLayout
import javax.swing.JPanel


class CharacterCardList(cardWidth: Int) : JPanel(MigLayout("insets 0")) {

    private var fixedCellWidth = 0
    private var fixedCellHeight = 0
    private var selectedCharacter: DofusCharacter? = CharacterManager.getCurrentCharacter()
    private val dragManager = DragManager(this)
    private val cards = ArrayList<CharacterCard>()

    init {
        fixedCellHeight = cardWidth / 4
        fixedCellWidth = cardWidth
        for (character in CharacterManager.getCharacters()) {
            addCharacterCard(character)
        }
    }

    fun addCharacterCard(character: DofusCharacter) {
        val characterCard = CharacterCard(character)
        val selected = selectedCharacter == character
        characterCard.initialize(selected, fixedCellWidth, fixedCellHeight)
        val listener = CharacterCardListener(this, characterCard, dragManager)
        characterCard.addMouseListener(listener)
        characterCard.addMouseMotionListener(listener)
        cards.add(characterCard)
        insertCard(characterCard)
        updateUI()
    }

    fun selectCharacter(characterCard: CharacterCard) {
        CharacterManager.setCurrentCharacter(characterCard.character)
        selectedCharacter = characterCard.character
        cards.forEach { it.update(false) }
        characterCard.update(true)
        updateUI()
        MainPanel.addCharacterScriptTab(characterCard.character)
    }

    fun removeCharacter(characterCard: CharacterCard) {
        CharacterManager.removeCharacter(characterCard.character)
        cards.remove(characterCard)
        remove(characterCard)
        updateUI()
    }

    private fun insertCard(characterCard: CharacterCard, index: Int = -1) {
        add(characterCard, "width $fixedCellWidth, height $fixedCellHeight, wrap", index)
    }

    fun moveElement(fromCard: CharacterCard, toCard: CharacterCard) {
        val toIndex = cards.indexOf(toCard)
        if (cards.indexOf(fromCard) != toIndex) {
            cards.remove(fromCard)
            remove(fromCard)
            cards.add(toIndex, fromCard)
            insertCard(fromCard, toIndex)
            updateUI()
        }
    }

}