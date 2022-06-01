package fr.lewon.dofus.bot.util

abstract class Listenable<T> {

    protected val listeners = ArrayList<T>()

    fun addListener(listener: T) {
        listeners.add(listener)
    }

    fun removeListener(listener: T) {
        listeners.remove(listener)
    }
}