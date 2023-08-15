package fr.lewon.dofus.bot.util.geometry

interface IRectangle<R : IRectangle<R, T>, T : IPoint<T, out Number>> {

    fun getCenter(): T
    fun getCenterLeft(): T
    fun getCenterRight(): T
    fun getTopCenter(): T
    fun getTopLeft(): T
    fun getTopRight(): T
    fun getBottomCenter(): T
    fun getBottomLeft(): T
    fun getBottomRight(): T
    fun getTranslation(delta: T): R

}