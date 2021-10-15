package fr.lewon.dofus.bot.gui

import fr.lewon.dofus.bot.gui.tabs.about.AboutTab
import fr.lewon.dofus.bot.gui.tabs.characters.CharactersTab
import fr.lewon.dofus.bot.gui.tabs.config.ConfigTab
import fr.lewon.dofus.bot.gui.tabs.exec.ExecutionTab
import fr.lewon.dofus.bot.gui.tabs.scripts.ScriptTab
import fr.lewon.dofus.bot.util.filemanagers.CharacterManager
import javax.swing.JTabbedPane

object MainPanel : JTabbedPane() {

    init {
        addTab("Config", ConfigTab)
        addTab("Characters", CharactersTab)
        addTab("Scripts", ScriptTab)
        addTab("Logs", ExecutionTab)
        addTab("About", AboutTab)
        refreshScriptsTab()
    }

    fun refreshScriptsTab() {
        val enabled = CharacterManager.getCurrentCharacter() != null
        setEnabledAt(indexOfTab("Scripts"), enabled)
        if (enabled) {
            ScriptTab.updateScript()
        }
    }

}