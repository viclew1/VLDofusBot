package fr.lewon.dofus.bot.gui.vldb.panes.script

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScripts
import javax.swing.JComboBox

class DofusScriptComboBox :
    JComboBox<DofusBotScript>(DofusBotScripts.values().map { it.buildScript() }.toTypedArray()) {

    init {
        setRenderer(DofusBotScriptRenderer())
    }

}