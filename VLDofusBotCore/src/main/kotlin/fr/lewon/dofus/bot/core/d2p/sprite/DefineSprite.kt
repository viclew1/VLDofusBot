package fr.lewon.dofus.bot.core.d2p.sprite

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.exporters.commonshape.ExportRectangle
import com.jpexs.decompiler.flash.exporters.commonshape.Matrix
import com.jpexs.decompiler.flash.tags.DefineSpriteTag
import com.jpexs.decompiler.flash.tags.base.RenderContext
import com.jpexs.decompiler.flash.timeline.Timeline
import com.jpexs.helpers.SerializableImage
import fr.lewon.dofus.bot.core.ui.UIPoint
import fr.lewon.dofus.bot.core.ui.UIRectangle
import java.awt.image.BufferedImage

class DefineSprite(val tag: DefineSpriteTag) {

    fun getBounds(horizontalSymmetry: Boolean): UIRectangle {
        val r = tag.timeline.displayRect
        return UIRectangle(
            UIPoint(
                if (horizontalSymmetry) {
                    ((-r.Xmin - r.width) / SWF.unitDivisor).toFloat()
                } else {
                    (r.Xmin / SWF.unitDivisor).toFloat()
                },
                (r.Ymin / SWF.unitDivisor).toFloat()
            ),
            UIPoint((r.width / SWF.unitDivisor).toFloat(), (r.height / SWF.unitDivisor).toFloat())
        )
    }

    /*
     * Based on https://github.com/jindrapetrik/jpexs-decompiler/blob/master/libsrc/ffdec_lib/src/com/jpexs/decompiler/flash/SWF.java#L3461
     */
    fun getImage(horizontalSymmetry: Boolean): BufferedImage {
        val tim = tag.timeline
        val r = tim.displayRect

        val zoom = 1.0
        val w = (r.width * zoom / SWF.unitDivisor).toInt()
        val h = (r.height * zoom / SWF.unitDivisor).toInt()

        val image = SerializableImage(w, h, SerializableImage.TYPE_INT_ARGB_PRE)
        image.fillTransparent() // Is that necessary?

        val m = Matrix()
        m.translate(
            (if (horizontalSymmetry) r.Xmin + r.width else -r.Xmin) * zoom,
            -r.Ymin * zoom
        )
        m.scale(zoom * if (horizontalSymmetry) -1 else 1, zoom)

        tim.toImage(0, 0, RenderContext(), image, image, false, m, Matrix(), m, null, zoom, false, ExportRectangle(r), m, true, Timeline.DRAW_MODE_ALL)

        return image.bufferedImage
    }
}
