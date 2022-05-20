package fr.lewon.dofus.bot.gui.custom.list

import fr.lewon.dofus.bot.gui.util.UiResource
import java.awt.event.ActionListener
import javax.swing.JButton

class CardButtonInfo(
    val title: String,
    val uiResource: UiResource,
    val button: JButton = JButton(),
    val actionListener: ActionListener,
)