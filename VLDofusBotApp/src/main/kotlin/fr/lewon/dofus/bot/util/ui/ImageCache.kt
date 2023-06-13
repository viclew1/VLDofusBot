package fr.lewon.dofus.bot.util.ui

import androidx.compose.ui.graphics.painter.Painter
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import java.util.concurrent.locks.ReentrantLock

abstract class ImageCache<T> {

    private val loadedKeys = ArrayList<T>()
    private val painterByImageUrl = HashMap<T, Painter?>()
    private val lock = ReentrantLock()

    fun trim(toKeepKeys: List<T>) = lock.executeSyncOperation {
        val toRemoveKeys = loadedKeys.toMutableSet()
        toRemoveKeys.removeAll(toKeepKeys.toSet())
        loadedKeys.removeAll(toRemoveKeys)
        painterByImageUrl.keys.removeAll(toRemoveKeys)
    }

    fun getPainter(key: T) = lock.executeSyncOperation {
        painterByImageUrl[key]
    }

    fun isLoaded(key: T) = lock.executeSyncOperation {
        loadedKeys.contains(key)
    }

    fun loadImagePainter(key: T) = lock.executeSyncOperation {
        loadedKeys.add(key)
        Thread {
            val painter = doLoadImage(key)
            lock.executeSyncOperation {
                painterByImageUrl[key] = painter
            }
        }.start()
    }

    abstract fun doLoadImage(key: T): Painter?

}