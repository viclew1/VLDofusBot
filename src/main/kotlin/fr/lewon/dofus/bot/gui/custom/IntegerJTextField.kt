package fr.lewon.dofus.bot.gui.custom

import java.awt.Toolkit
import java.awt.event.FocusEvent
import javax.swing.text.AttributeSet
import javax.swing.text.Document
import javax.swing.text.PlainDocument


class IntegerJTextField(value: Int = 0) : CustomJTextField(value.toString()) {

    override fun createDefaultModel(): Document {
        return object : PlainDocument() {

            override fun insertString(offs: Int, str: String?, a: AttributeSet?) {
                if (offs == 0 && str == "-" && !text.contains("-")) {
                    super.insertString(offs, str, a)
                } else if (str?.toIntOrNull() != null) {
                    super.insertString(offs, str, a)
                } else {
                    Toolkit.getDefaultToolkit().beep()
                }
            }

        }
    }


    override fun processFocusEvent(e: FocusEvent?) {
        if (text == "-" || text.isEmpty()) {
            text = "0"
        }
        super.processFocusEvent(e)
    }

}