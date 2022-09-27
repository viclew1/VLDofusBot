package fr.lewon.dofus.bot.gui2.main.metamob.filter

import javax.swing.JPanel

object MonsterFilterGlobalPanel : JPanel() {

    init {
        add(MonsterFilterPanel, "w max, h max, wrap")
        add(MonsterFilterStatsPanel, "w max, h 150:150:150, dock south")
    }

}