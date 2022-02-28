package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.panes.execution.CharacterLogsPanel
import fr.lewon.dofus.bot.gui.panes.script.selector.CharacterScriptSelectorPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class CharacterScriptPanel(val character: DofusCharacter) : JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    init {
        val selectorPanel = CharacterScriptSelectorPanel(character)
        val logsPanel = CharacterLogsPanel(character)
        add(selectorPanel, "w 340:340:340, h 0:max:max")
        add(logsPanel, "w 0:max:max, h 0:max:max")
        selectorPanel.border = BorderFactory.createEtchedBorder()
        logsPanel.border = BorderFactory.createEtchedBorder()
    }

}