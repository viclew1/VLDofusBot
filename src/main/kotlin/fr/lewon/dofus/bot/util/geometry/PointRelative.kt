package fr.lewon.dofus.bot.util.geometry

class PointRelative(var x: Float = 0f, var y: Float = 0f) : IPoint<PointRelative, Float> {

    override fun getSum(point: PointRelative): PointRelative {
        return PointRelative(x + point.x, y + point.y)
    }

    override fun getDifference(point: PointRelative): PointRelative {
        return PointRelative(x - point.x, y - point.y)
    }

    override fun getProduct(prod: Float): PointRelative {
        return PointRelative(x * prod, y * prod)
    }

    override fun opposite(): PointRelative {
        return PointRelative(-x, -y)
    }
}