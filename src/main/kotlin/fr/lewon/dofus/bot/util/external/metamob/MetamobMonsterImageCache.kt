package fr.lewon.dofus.bot.util.external.metamob

import fr.lewon.dofus.bot.gui2.util.ImageUtil
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import java.awt.image.BufferedImage

object MetamobMonsterImageCache {

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