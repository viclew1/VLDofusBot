import fr.lewon.dofus.bot.sniffer.DofusMessageReceiverUtil
import java.io.File

fun main() {
    clearSubDirs(EXPORT_DIR + DEST_TYPES_PATH.replace(".", "/"))
    clearSubDirs(EXPORT_DIR + DEST_MESSAGES_PATH.replace(".", "/"))
    DofusMessageReceiverUtil.prepareNetworkManagers(listOf(MessageExportBuilder))
}

private fun clearSubDirs(path: String) {
    println("Clearing directory : $path")
    File(path).listFiles()?.filter { it.isDirectory }?.forEach(::deleteDirectory)
}

private fun deleteDirectory(directory: File, indentLevel: Int = 0) {
    if (!directory.isDirectory) {
        error("Not a directory : ${directory.absolutePath}")
    }
    println("${"  ".repeat(indentLevel)}- ${directory.name} :")
    val files = directory.listFiles() ?: emptyArray()
    for (file in files) {
        if (file.isDirectory) {
            deleteDirectory(file, indentLevel + 1)
        } else {
            println("${"  ".repeat(indentLevel + 1)}- ${file.name}")
            file.delete()
        }
    }
    directory.delete()
}
