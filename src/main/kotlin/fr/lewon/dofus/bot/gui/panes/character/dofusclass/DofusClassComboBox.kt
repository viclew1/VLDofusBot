package fr.lewon.dofus.bot.gui.panes.character.dofusclass

import fr.lewon.dofus.bot.model.characters.DofusClass
import javax.swing.JComboBox


class DofusClassComboBox : JComboBox<DofusClass>(DofusClass.values()) {

    init {
        setRenderer(DofusClassRenderer())
    }

}