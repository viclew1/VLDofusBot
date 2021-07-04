package fr.lewon.dofus.bot.gui

import fr.lewon.dofus.bot.gui.about.AboutTab
import fr.lewon.dofus.bot.gui.characters.CharactersTab
import fr.lewon.dofus.bot.gui.config.ConfigTab
import fr.lewon.dofus.bot.gui.scripts.ScriptTab
import fr.lewon.dofus.bot.util.filemanagers.DTBCharacterManager
import javax.swing.JTabbedPane

object MainPanel : JTabbedPane() {

    init {
        addTab("Config", ConfigTab)
        addTab("Characters", CharactersTab)
        addTab("Scripts", ScriptTab)
        addTab("About", AboutTab)
        refreshScriptsTab()
    }

    fun refreshScriptsTab() {
        val enabled = DTBCharacterManager.getCurrentCharacter() != null
        setEnabledAt(indexOfTab("Scripts"), enabled)
        if (enabled) {
            ScriptTab.updateScript()
        }
    }

}