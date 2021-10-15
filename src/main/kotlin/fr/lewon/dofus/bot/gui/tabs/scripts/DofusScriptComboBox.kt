package fr.lewon.dofus.bot.gui.tabs.scripts

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScripts
import javax.swing.JComboBox

class DofusScriptComboBox : JComboBox<DofusBotScript>(DofusBotScripts.values().map { it.script }.toTypedArray()) {

    init {
        setRenderer(DofusBotScriptRenderer())
    }

}