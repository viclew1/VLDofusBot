package fr.lewon.dofus.bot.util.listenable

import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import java.util.concurrent.locks.ReentrantLock

abstract class Listenable<T> {

    private val listeners = ArrayList<T>()
    private val lock = ReentrantLock()

    protected fun getListeners(): List<T> {
        return lock.executeSyncOperation { listeners }
    }

    fun addListener(listener: T) {
        lock.executeSyncOperation {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: T) {
        lock.executeSyncOperation {
            listeners.remove(listener)
        }
    }

}