package fr.lewon.dofus.bot.util.filemanagers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

abstract class FileManager<T>(private val fileName: String, defaultStore: T) : ToInitManager {

    protected var store = defaultStore
    private lateinit var storeFile: File

    override fun initManager() {
        storeFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/$fileName")
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        if (storeFile.exists()) {
            store = mapper.readValue(storeFile, getStoreClass())
        } else {
            saveStore()
        }
    }

    protected abstract fun getStoreClass(): Class<T>

    protected fun saveStore() {
        with(OutputStreamWriter(FileOutputStream(storeFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(store))
            close()
        }
    }

}