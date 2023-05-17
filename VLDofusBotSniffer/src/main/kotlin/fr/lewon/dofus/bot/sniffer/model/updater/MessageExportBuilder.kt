package fr.lewon.dofus.bot.sniffer.model.updater

import com.jpexs.decompiler.flash.abc.ScriptPack
import fr.lewon.dofus.export.builder.VldbAbstractExportPackTaskBuilder
import java.io.File
import java.util.*


object MessageExportBuilder : VldbAbstractExportPackTaskBuilder() {

    override fun treatFileContent(fileContent: String, scriptPack: ScriptPack) {
        val path = scriptPack.classPath.packageStr.toFilePath()
            .replace("\\", ".")
            .replace("/", ".")
        val regex = Regex("([^a-zA-Z\\d])object([^a-zA-Z\\d]|Func\\(|treeFunc\\()")
        val treatedFileContent = regex.replace(fileContent) {
            "${it.destructured.component1()}obj${it.destructured.component2()}"
        }
        val ktContent = FlashToKotlinGenerator.generateClass(path, scriptPack.name, treatedFileContent)
        val destPath = path.replace(BASE_MESSAGES_PATH, DEST_MESSAGES_PATH)
            .replace(BASE_TYPES_PATH, DEST_TYPES_PATH)
            .replace(".", "/")
        val ktFile = File("$EXPORT_DIR$destPath/${scriptPack.name}.kt")
        val toCreateDirectories = LinkedList<File>()
        var parent = ktFile.parentFile
        while (!parent.exists()) {
            toCreateDirectories.addFirst(parent)
            parent = parent.parentFile
        }
        toCreateDirectories.forEach(File::mkdir)
        ktFile.writeText(ktContent)
    }

    override fun matchesScriptPack(scriptPack: ScriptPack): Boolean =
        scriptPack.path.startsWith(BASE_MESSAGES_PATH) || scriptPack.path.startsWith(BASE_TYPES_PATH)
}