package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import fr.lewon.dofus.bot.gui2.main.metamob.filter.MonsterFilterPanel
import fr.lewon.dofus.bot.gui2.main.metamob.filter.MonsterFilterStatsPanel
import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.gui2.util.AppFonts
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

object MonsterListPanel : JPanel() {

    private const val MONSTER_CARD_WIDTH = 180
    private const val MONSTER_CARD_HEIGHT = 80
    private const val IMAGE_HEIGHT = MONSTER_CARD_HEIGHT * 2 / 3
    private const val BOTTOM_COLOR_HEIGHT = 5
    private const val CARD_MARGIN = 2

    private val MONSTER_CARD_BACKGROUND_COLOR = Color(245, 236, 221)
    private val OWNED_MONSTER_COLOR = Color(92, 184, 92)
    private val MISSING_MONSTER_COLOR = Color(237, 17, 29)

    private var monsters: List<MetamobMonster> = emptyList()
    private val visibleMonsters = ArrayList<MetamobMonster>()
    private val monsterImagesLoaded = ArrayList<String>()
    private val imageByUrl = HashMap<String, BufferedImage?>()

    fun updateMonsters(monsters: List<MetamobMonster>) {
        MonsterListPanel.monsters = monsters
        updateFilters()
    }

    fun updateFilters() {
        visibleMonsters.clear()
        monsters.forEach {
            if (MonsterFilterPanel.FILTERS.all { filter ->
                    filter.isMonsterValidFun(
                        filter.parameter.defaultValue,
                        it
                    )
                }) {
                visibleMonsters.add(it)
            }
        }
        paintComponent(null)
        MonsterFilterStatsPanel.update(visibleMonsters)
        updateUI()
    }

    override fun paintComponent(g: Graphics?) {
        if (g != null) {
            super.paintComponent(g)
        }
        var currentCount = 0
        val cardWidth = CARD_MARGIN + MONSTER_CARD_WIDTH
        val cardHeight = CARD_MARGIN + MONSTER_CARD_HEIGHT
        val maxByRow = width / cardWidth
        if (maxByRow == 0) {
            return
        }
        var y = 0
        visibleMonsters.forEach { monster ->
            y = (currentCount / maxByRow) * cardHeight
            if (y + this.y + cardHeight > 0 && y + this.y <= parent.height) {
                val x = (currentCount % maxByRow) * cardWidth
                if (g != null) {
                    drawMonster(g, monster, x, y)
                }
            }
            currentCount++
        }
        val size = Dimension(width, y + cardHeight)
        minimumSize = size
        preferredSize = size
        maximumSize = size
    }

    private fun drawMonster(g: Graphics, monster: MetamobMonster, x: Int, y: Int) {
        drawBackground(g, monster, x, y)
        drawMonsterImage(g, monster, x, y)
        drawMonsterInfo(g, monster, x, y)
        g.color = Color.BLACK
        g.drawRect(x, y, MONSTER_CARD_WIDTH, MONSTER_CARD_HEIGHT)
    }

    private fun drawBackground(g: Graphics, monster: MetamobMonster, x: Int, y: Int) {
        g.color = MONSTER_CARD_BACKGROUND_COLOR
        g.fillRect(x, y, MONSTER_CARD_WIDTH, MONSTER_CARD_HEIGHT)
        g.color = if (monster.amount <= 0) MISSING_MONSTER_COLOR else OWNED_MONSTER_COLOR
        g.fillRect(x, y + MONSTER_CARD_HEIGHT - BOTTOM_COLOR_HEIGHT, MONSTER_CARD_WIDTH, BOTTOM_COLOR_HEIGHT)
    }

    private fun drawMonsterImage(g: Graphics, monster: MetamobMonster, x: Int, y: Int) {
        getImage(monster)?.let {
            val realX = x + MONSTER_CARD_WIDTH - it.width - BOTTOM_COLOR_HEIGHT
            val realY = y + MONSTER_CARD_HEIGHT - it.height - BOTTOM_COLOR_HEIGHT
            g.drawImage(it, realX, realY, null)
        }
    }

    private fun drawMonsterInfo(g: Graphics, monster: MetamobMonster, x: Int, y: Int) {
        g.color = Color.BLACK
        g.font = AppFonts.DEFAULT_FONT
        g.drawString(monster.name, x + 5, y + MONSTER_CARD_HEIGHT / 4)
        g.drawString("Owned : ${monster.amount}", x + 5, y + MONSTER_CARD_HEIGHT * 2 / 4)
        val status = if (monster.searched > 0) "Searched" else if (monster.offered > 0) "Offered" else "/"
        g.drawString("Status : $status", x + 5, y + MONSTER_CARD_HEIGHT * 3 / 4)
    }

    private fun getImage(monster: MetamobMonster): BufferedImage? {
        if (!monsterImagesLoaded.contains(monster.imageUrl)) {
            Thread {
                imageByUrl[monster.imageUrl] = MonsterImageCache.getImage(monster, IMAGE_HEIGHT)
                updateUI()
            }.start()
            monsterImagesLoaded.add(monster.imageUrl)
        }
        return imageByUrl[monster.imageUrl]
    }
}