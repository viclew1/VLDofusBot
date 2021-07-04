package fr.lewon.dofus.bot.gui.custom

import java.awt.Toolkit
import javax.swing.JTextField
import javax.swing.text.AttributeSet
import javax.swing.text.Document
import javax.swing.text.PlainDocument

class IntegerJTextField(value: Int = 0) : JTextField(value.toString()) {

    override fun createDefaultModel(): Document {
        return object : PlainDocument() {
            override fun insertString(offs: Int, str: String?, a: AttributeSet?) {
                str?.toIntOrNull()?.let {
                    super.insertString(offs, str, a)
                } ?: Toolkit.getDefaultToolkit().beep()
            }
        }
    }
}