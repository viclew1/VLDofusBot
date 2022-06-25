package fr.lewon.dofus.bot.gui2.main.metamob.filter

import net.miginfocom.swing.MigLayout
import javax.swing.JPanel

object MonsterFilterGlobalPanel : JPanel(MigLayout("")) {

    init {
        add(MonsterFilterPanel, "w max, h max, wrap")
        add(MonsterFilterStatsPanel, "w max, h 150:150:150, dock south")
    }

}