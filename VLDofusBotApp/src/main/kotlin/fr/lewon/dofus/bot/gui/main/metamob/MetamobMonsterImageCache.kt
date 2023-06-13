package fr.lewon.dofus.bot.gui.main.metamob

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.d2p.gfx.D2PMonstersGfxAdapter
import fr.lewon.dofus.bot.gui.util.getBufferedImage
import fr.lewon.dofus.bot.gui.util.trimImage
import fr.lewon.dofus.bot.util.external.metamob.MetamobMonstersHelper
import fr.lewon.dofus.bot.util.external.metamob.model.MetamobMonster
import fr.lewon.dofus.bot.util.ui.ImageCache

object MetamobMonsterImageCache : ImageCache<MetamobMonster>() {

    @Synchronized
    override fun doLoadImage(key: MetamobMonster): Painter? {
        val monster = MetamobMonstersHelper.getDofusMonster(key)
            ?: return null
        val iconData = D2PMonstersGfxAdapter.getMonsterImageData(monster.id)
        return iconData.getBufferedImage().trimImage().toPainter()
    }
}