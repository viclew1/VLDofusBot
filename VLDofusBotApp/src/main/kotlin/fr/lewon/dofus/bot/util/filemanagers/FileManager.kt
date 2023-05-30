package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock

class FileManager<T : Any>(fileName: String, defaultStore: T) {

    private val store: T
    private val storeFile: File = File("${VldbFilesUtil.getVldbConfigDirectory()}/$fileName")
    private val lock = ReentrantLock()

    init {
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (storeFile.exists()) {
            store = mapper.readValue(storeFile, defaultStore::class.java)
        } else {
            store = defaultStore
            saveStore()
        }
    }

    fun getStore(): T = store

    fun updateStore(update: (T) -> Unit) = lock.executeSyncOperation {
        update(store)
        saveStore()
    }

    fun <R> getElement(get: (T) -> R): R = lock.executeSyncOperation {
        get(store)
    }

    private fun saveStore() = with(OutputStreamWriter(FileOutputStream(storeFile, false), StandardCharsets.UTF_8)) {
        write(ObjectMapper().writeValueAsString(store))
        close()
    }

}