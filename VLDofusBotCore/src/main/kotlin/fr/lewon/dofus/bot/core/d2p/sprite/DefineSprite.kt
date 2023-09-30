package fr.lewon.dofus.bot.core.d2p.sprite

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.exporters.commonshape.ExportRectangle
import com.jpexs.decompiler.flash.exporters.commonshape.Matrix
import com.jpexs.decompiler.flash.tags.DefineSpriteTag
import com.jpexs.decompiler.flash.tags.base.RenderContext
import com.jpexs.decompiler.flash.timeline.Timeline
import com.jpexs.helpers.SerializableImage
import java.awt.Rectangle
import java.awt.image.BufferedImage

class DefineSprite(val tag: DefineSpriteTag) {

    fun getBounds(): Rectangle {
        val r = tag.timeline.displayRect
        return Rectangle(
            (r.Xmin / SWF.unitDivisor).toInt(),
            (r.Ymin / SWF.unitDivisor).toInt(),
            (r.width / SWF.unitDivisor).toInt(),
            (r.height / SWF.unitDivisor).toInt()
        )
    }

    /*
     * Based on https://github.com/jindrapetrik/jpexs-decompiler/blob/master/libsrc/ffdec_lib/src/com/jpexs/decompiler/flash/SWF.java#L3461
     */
    fun getImage(): BufferedImage {
        val tim = tag.timeline
        val rect = tim.displayRect

        val zoom = 1.0
        val w = (rect.width * zoom / SWF.unitDivisor).toInt()
        val h = (rect.height * zoom / SWF.unitDivisor).toInt()

        val image = SerializableImage(w, h, SerializableImage.TYPE_INT_ARGB_PRE)
        image.fillTransparent() // Is that necessary?

        val m = Matrix()
        m.translate(-rect.Xmin * zoom, -rect.Ymin * zoom)
        m.scale(zoom)

        tim.toImage(0, 0, RenderContext(), image, image, false, m, Matrix(), m, null, zoom, false, ExportRectangle(rect), m, true, Timeline.DRAW_MODE_ALL)

        return image.bufferedImage
    }
}
