package fr.lewon.dofus.bot.gui.main.treasure

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import fr.lewon.dofus.bot.core.d2p.gfx.D2PWorldGfxAdapter
import fr.lewon.dofus.bot.gui.util.getBufferedImage
import fr.lewon.dofus.bot.gui.util.trimImage
import fr.lewon.dofus.bot.util.ui.ImageCache

class TreasureHintImageCache : ImageCache<Int>() {

    @Synchronized
    override fun doLoadImage(key: Int): Painter {
        val iconData = D2PWorldGfxAdapter.getWorldGfxImageData(key.toDouble())
        return iconData.getBufferedImage().trimImage().toPainter()
    }
}