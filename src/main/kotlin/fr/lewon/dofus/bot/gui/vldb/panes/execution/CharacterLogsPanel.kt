package fr.lewon.dofus.bot.gui.vldb.panes.execution

import fr.lewon.dofus.bot.model.characters.DofusCharacter
import javax.swing.JTabbedPane

class CharacterLogsPanel(character: DofusCharacter) : JTabbedPane() {

    private val executionLogsScrollPane = LogsPanel(character.executionLogger)
    private val snifferLogsScrollPane = LogsPanel(character.snifferLogger, true)

    init {
        addTab("Execution logs", executionLogsScrollPane)
        addTab("Sniffer logs", snifferLogsScrollPane)
    }

}