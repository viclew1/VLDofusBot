package fr.lewon.dofus.bot.gui.panes.script

import fr.lewon.dofus.bot.gui.panes.execution.CharacterLogsPanel
import fr.lewon.dofus.bot.model.characters.DofusCharacter
import net.miginfocom.swing.MigLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class GlobalScriptPanel(character: DofusCharacter) : JPanel(MigLayout("insets 0, gapX 0, gapY 0")) {

    init {
        val scriptPanel = CharacterScriptPanel(character)
        val logsPanel = CharacterLogsPanel(character)
        add(scriptPanel, "w 340:340:340, h 0:max:max")
        add(logsPanel, "w 0:max:max, h 0:max:max")
        scriptPanel.border = BorderFactory.createEtchedBorder()
        logsPanel.border = BorderFactory.createEtchedBorder()
    }

}