package fr.lewon.dofus.bot.gui2.main.metamob.filter

import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonsterType
import fr.lewon.dofus.bot.gui2.util.AppFonts
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

object MonsterFilterStatsPanel : JPanel(MigLayout()) {

    private val totalDisplayedLabel = JLabel("Total displayed : ")
    private val totalDisplayedValue = JLabel("0")
    private val totalArchmonsterLabel = JLabel("Archmonsters : ")
    private val totalArchmonsterValue = JLabel("0")
    private val totalBossLabel = JLabel("Bosses : ")
    private val totalBossValue = JLabel("0")
    private val totalMonsterLabel = JLabel("Monsters : ")
    private val totalMonsterValue = JLabel("0")

    private val filterLabel = JLabel("Stats").also { it.font = AppFonts.TITLE_FONT }
    private val statsScrollPane = JScrollPane()

    init {
        val statsPanel = JPanel(MigLayout())
        statsPanel.add(totalDisplayedLabel)
        statsPanel.add(totalDisplayedValue, "wrap")
        statsPanel.add(totalArchmonsterLabel)
        statsPanel.add(totalArchmonsterValue, "wrap")
        statsPanel.add(totalBossLabel)
        statsPanel.add(totalBossValue, "wrap")
        statsPanel.add(totalMonsterLabel)
        statsPanel.add(totalMonsterValue, "wrap")

        add(filterLabel, "wrap")
        statsScrollPane.horizontalScrollBar = null
        add(statsScrollPane, "h max, width max, wrap")
        statsScrollPane.setViewportView(statsPanel)
    }

    fun update(displayedMonsters: List<MetamobMonster>) {
        val archmonsters = displayedMonsters.filter { it.type == MetamobMonsterType.ARCHMONSTER }
        val bosses = displayedMonsters.filter { it.type == MetamobMonsterType.BOSS }
        val monsters = displayedMonsters.filter { it.type == MetamobMonsterType.MONSTER }
        val totalCount = displayedMonsters.size
        val totalMissing = displayedMonsters.count { it.amount <= 0 }
        val archmonstersCount = archmonsters.size
        val archmonstersMissing = archmonsters.count { it.amount <= 0 }
        val bossesCount = bosses.size
        val bossesMissing = bosses.count { it.amount <= 0 }
        val monstersCount = monsters.size
        val monstersMissing = monsters.count { it.amount <= 0 }
        totalDisplayedValue.text = "$totalCount ($totalMissing missing)"
        totalArchmonsterValue.text = "$archmonstersCount ($archmonstersMissing missing)"
        totalBossValue.text = "$bossesCount ($bossesMissing missing)"
        totalMonsterValue.text = "$monstersCount ($monstersMissing missing)"
    }

}