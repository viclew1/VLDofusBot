package fr.lewon.dofus.bot.core.d2o

import fr.lewon.dofus.bot.core.d2o.gamedata.GameDataClassDefinition
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import java.io.File
import java.util.concurrent.locks.ReentrantLock

object D2OUtil {

    private val lock = ReentrantLock()
    private val streamsByModuleName = HashMap<String, ByteArrayReader>()
    private val streamStartIndexesByModuleName = HashMap<String, Int>()
    private val classesByModuleName = HashMap<String, HashMap<Int, GameDataClassDefinition>>()
    private val counterByModuleName = HashMap<String, Int>()
    private val indexesByModuleName = HashMap<String, HashMap<Int, Int>>()

    fun getClassDefinition(moduleName: String, classId: Int): GameDataClassDefinition {
        val classes = classesByModuleName[moduleName]
            ?: error("no classes for module name : $moduleName")
        return classes[classId] ?: error("No class definition for classId : $classId")
    }

    fun getModuleNames(): List<String> {
        return streamsByModuleName.keys.toList()
    }

    fun getObjects(moduleName: String): List<Map<String, Any>> {
        return lock.executeSyncOperation {
            val stream = streamsByModuleName[moduleName]
                ?: error("Didn't load stream for module : $moduleName")
            val classes = classesByModuleName[moduleName]
                ?: error("Didn't load classes for module : $moduleName")
            val streamStartIndex = streamStartIndexesByModuleName[moduleName]
                ?: error("Didn't load stream start index for module : $moduleName")
            val count = counterByModuleName[moduleName]
                ?: error("Didn't load count for module : $moduleName")

            stream.setPosition(streamStartIndex)
            val objects = ArrayList<Map<String, Any>>()
            for (i in 0 until count) {
                val classDef = classes[stream.readInt()] ?: error("Failed to load class definitions")
                objects.add(classDef.read(moduleName, stream))
            }
            objects
        }
    }

    fun init(filePath: String) {
        val file = File(filePath)
        val moduleName = file.name.substring(0, file.name.indexOf(".d2o"))
        val stream = streamsByModuleName.computeIfAbsent(moduleName) { ByteArrayReader(file.readBytes()) }
        initFromStream(stream, moduleName)
    }

    private fun initFromStream(stream: ByteArrayReader, moduleName: String) {
        stream.setPosition(0)
        streamStartIndexesByModuleName.computeIfAbsent(moduleName) { 7 }
        require("D2O" == stream.readString(3)) { error("Invalid D2O file") }

        val indexes = HashMap<Int, Int>()
        indexesByModuleName[moduleName] = indexes

        val indexesPointer = stream.readInt()
        stream.setPosition(indexesPointer)

        val indexesLength = stream.readInt()
        var count = 0
        for (i in 0 until indexesLength step 8) {
            val key = stream.readInt()
            val pointer = stream.readInt()
            indexes[key] = pointer
            count++
        }
        counterByModuleName[moduleName] = count

        val classes = HashMap<Int, GameDataClassDefinition>()
        classesByModuleName[moduleName] = classes
        val classesCount = stream.readInt()
        for (i in 0 until classesCount) {
            readClassDefinition(stream.readInt(), stream, classes)
        }
    }

    private fun readClassDefinition(
        classId: Int, stream: ByteArrayReader, classes: HashMap<Int, GameDataClassDefinition>
    ) {
        stream.readUTF()
        stream.readUTF()
        val classDef = GameDataClassDefinition()
        val fieldsCount = stream.readInt()
        for (i in 0 until fieldsCount) {
            val fieldName = stream.readUTF()
            classDef.addField(fieldName, stream)
        }
        classes[classId] = classDef
    }

}