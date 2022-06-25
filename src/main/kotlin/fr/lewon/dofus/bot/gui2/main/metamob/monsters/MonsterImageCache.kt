package fr.lewon.dofus.bot.gui2.main.metamob.monsters

import fr.lewon.dofus.bot.gui2.main.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.gui2.main.metamob.util.MetamobRequestProcessor
import fr.lewon.dofus.bot.gui2.util.ImageUtil
import java.awt.image.BufferedImage

object MonsterImageCache {

    private val imageByUrl = HashMap<String, BufferedImage?>()

    @Synchronized
    fun getImage(monster: MetamobMonster, height: Int): BufferedImage? {
        return imageByUrl.computeIfAbsent(monster.imageUrl) {
            MetamobRequestProcessor.getImage(it)?.let { image ->
                ImageUtil.getScaledImageKeepHeight(image, height)
            }
        }
    }

}