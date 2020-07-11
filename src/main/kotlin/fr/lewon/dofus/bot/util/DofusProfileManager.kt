package fr.lewon.dofus.bot.util

import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object DofusProfileManager {

    private val PROFILE_FILES = listOf(
        "atouin.dat",
        "berilia.dat",
        "Berilia_binds.dat",
        "Berilia_ui_positions.dat",
        "clientData.dat",
        "dofus.dat",
        "tiphon.dat",
        "tubul.dat",
        "uid.dat"
    )

    fun getDofusConfDirectory(): File {
        val confDirName = System.getProperty("user.home") + "/AppData/Roaming/Dofus/"
        val confDir = File(confDirName)
        if (!confDir.exists() || !confDir.isDirectory) {
            error("Path to Dofus configuration directory does not exist, it should be found at [$confDirName]")
        }
        return confDir
    }

    private fun getProfileDirectory(profileName: String): File {
        val profileDir = File("game_config/$profileName")
        if (!profileDir.exists() || !profileDir.isDirectory) {
            error("Profile [$profileName] does not exist")
        }
        return profileDir
    }

    fun applyProfile(controller: DofusTreasureBotGUIController, logItem: LogItem? = null, profileName: String) {
        val profileCheckLog = controller.log("Checking profile [$profileName] ...", logItem)
        val confFiles = getProfileDirectory(profileName).listFiles() ?: emptyArray()
        profileCheckLog.closeLog("OK")

        val dirCheckLog = controller.log("Checking config directory ...", logItem)
        val confDir = getDofusConfDirectory()
        dirCheckLog.closeLog("OK")

        val globalCopyLog = controller.log("Copying files to destination ...", logItem)
        confFiles.filter { PROFILE_FILES.contains(it.name) }
            .forEach {
                val copyLog = controller.log("Copying ${it.name} ...", globalCopyLog)
                Files.copy(
                    it.toPath(),
                    File(confDir.absolutePath + "/" + it.name).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                copyLog.closeLog("OK")
            }
        globalCopyLog.closeLog("OK")
    }

    fun createProfile(controller: DofusTreasureBotGUIController, logItem: LogItem? = null, profileName: String) {

        val profileCheckLog = controller.log("Checking profile [$profileName] ...", logItem)
        val profileDir = File("game_config/$profileName")
        if (profileDir.exists()) {
            error("Profile [$profileName] already exists")
        }
        profileCheckLog.closeLog("OK")

        val dirCheckLog = controller.log("Checking config directory ...", logItem)
        val confDir = getDofusConfDirectory()
        val confFiles = confDir.listFiles() ?: emptyArray()
        dirCheckLog.closeLog("OK")

        val globalSaveLog = controller.log("Saving config files ...", logItem)
        File("game_config/$profileName/").mkdir()
        confFiles.filter { PROFILE_FILES.contains(it.name) }
            .forEach {
                val saveLog = controller.log("Saving ${it.name} ...", globalSaveLog)
                Files.copy(it.toPath(), File("game_config/$profileName/${it.name}").toPath())
                saveLog.closeLog("OK")
            }
        globalSaveLog.closeLog("OK")
    }

    fun deleteProfile(controller: DofusTreasureBotGUIController, logItem: LogItem?, profileName: String) {
        val profileCheckLog = controller.log("Checking profile [$profileName] ...", logItem)
        val profileDir = File("game_config/$profileName")
        if (!profileDir.exists() || !profileDir.isDirectory) {
            error("Profile [$profileName] does not exist")
        }
        profileCheckLog.closeLog("OK")

        val globalDeleteLog = controller.log("Deleting files ...", logItem)
        val files = profileDir.listFiles() ?: emptyArray()
        files.forEach {
            val deleteLog = controller.log("Deleting $profileName/${it.name} ...", globalDeleteLog)
            Files.delete(it.toPath())
            deleteLog.closeLog("OK")
        }
        Files.delete(profileDir.toPath())
        globalDeleteLog.closeLog("OK")
    }

}