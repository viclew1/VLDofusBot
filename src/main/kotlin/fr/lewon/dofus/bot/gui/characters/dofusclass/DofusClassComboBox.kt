package fr.lewon.dofus.bot.gui.characters.dofusclass

import fr.lewon.dofus.bot.model.characters.DofusClass
import javax.swing.JComboBox


class DofusClassComboBox : JComboBox<DofusClass>(DofusClass.values()) {

    init {
        setRenderer(DofusClassRenderer())
    }

}