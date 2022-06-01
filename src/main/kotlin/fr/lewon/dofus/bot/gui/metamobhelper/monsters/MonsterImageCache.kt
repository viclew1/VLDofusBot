package fr.lewon.dofus.bot.gui.metamobhelper.monsters

import fr.lewon.dofus.bot.gui.metamobhelper.model.MetamobMonster
import fr.lewon.dofus.bot.gui.metamobhelper.util.MetamobRequestProcessor
import fr.lewon.dofus.bot.gui.util.ImageUtil
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