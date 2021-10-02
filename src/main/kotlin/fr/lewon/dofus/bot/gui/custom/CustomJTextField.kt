package fr.lewon.dofus.bot.gui.custom

import java.awt.event.FocusEvent
import javax.swing.JTextField

open class CustomJTextField(value: String? = null) : JTextField(value) {

    override fun processFocusEvent(e: FocusEvent?) {
        if (e?.id == FocusEvent.FOCUS_GAINED) {
            selectAll()
        }
        super.processFocusEvent(e)
    }

}