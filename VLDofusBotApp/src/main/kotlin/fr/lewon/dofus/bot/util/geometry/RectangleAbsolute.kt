package fr.lewon.dofus.bot.util.geometry

import java.awt.Rectangle

class RectangleAbsolute(val x: Int, val y: Int, val width: Int, val height: Int) :
    IRectangle<RectangleAbsolute, PointAbsolute> {

    companion object {

        fun build(topLeft: PointAbsolute, bottomRight: PointAbsolute): RectangleAbsolute {
            return RectangleAbsolute(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y)
        }

        fun build(rectangle: Rectangle): RectangleAbsolute {
            return RectangleAbsolute(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
        }
    }

    override fun getCenter(): PointAbsolute {
        return PointAbsolute(x + width / 2, y + height / 2)
    }

    override fun getCenterLeft(): PointAbsolute {
        return PointAbsolute(x, y + height / 2)
    }

    override fun getCenterRight(): PointAbsolute {
        return PointAbsolute(x + width, y + height / 2)
    }

    override fun getTopCenter(): PointAbsolute {
        return PointAbsolute(x + width / 2, y)
    }

    override fun getTopLeft(): PointAbsolute {
        return PointAbsolute(x, y)
    }

    override fun getTopRight(): PointAbsolute {
        return PointAbsolute(x + width, y)
    }

    override fun getBottomCenter(): PointAbsolute {
        return PointAbsolute(x + width / 2, y + height)
    }

    override fun getBottomLeft(): PointAbsolute {
        return PointAbsolute(x, y + height)
    }

    override fun getBottomRight(): PointAbsolute {
        return PointAbsolute(x + width, y + height)
    }

    override fun getTranslation(delta: PointAbsolute): RectangleAbsolute {
        return RectangleAbsolute(x + delta.x, y + delta.y, width, height)
    }
}