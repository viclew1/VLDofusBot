package fr.lewon.dofus.bot.util

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import java.awt.GraphicsDevice
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object WindowsUtil {

    private var gamePid: Long? = null

    private fun getHandle(): HWND? {
        val windows = ArrayList<HWND>()
        val pid = gamePid ?: return null
        User32.INSTANCE.EnumWindows({ hwnd, _ ->
            val pidRef = IntByReference()
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef)
            if (pidRef.value.toLong() == pid) {
                windows.add(hwnd)
            }
            true
        }, null)
        if (windows.isEmpty()) {
            return null
        }
        return windows[0]
    }

    fun isGameOpen(): Boolean {
        return getHandle() != null
    }

    fun openGame(controller: DofusTreasureBotGUIController, logItem: LogItem? = null) {
        val openingLog = controller.log("Opening game ...", logItem)
        if (isGameOpen()) {
            error("Game already opened")
        }
        val locateGameProcessBuilder = ProcessBuilder("cmd", "/c", "where Dofus.exe")
        val gameLocStr = readProcessResult(locateGameProcessBuilder.start())
            ?: error("Unable to find Dofus.exe, is it in your path?")
        val gameFile = File(gameLocStr)
        if (!gameFile.exists() || !gameFile.isFile) {
            error("Unable to find Dofus.exe, is it in your path?")
        }
        controller.log("Found the game at [$gameLocStr]", openingLog)
        val processBuilder = ProcessBuilder(
            "cmd",
            "/c",
            "wmic process call create \"${gameFile.absolutePath}\", \"${gameFile.parentFile.absolutePath}\" | findStr ProcessId"
        )
        val pidStr = readProcessResult(processBuilder.start(), "ProcessId = ([0-9]+);")
            ?: error("Failed to launch game")
        gamePid = pidStr.toLong()
        println(gamePid ?: "/")
        waitUntil({ getHandle() != null })
        waitUntil({ User32.INSTANCE.IsWindowEnabled(getHandle()) && User32.INSTANCE.IsWindowVisible(getHandle()) })
        controller.closeLog("Game opened", openingLog)
    }

    private fun readProcessResult(process: Process, regex: String = ""): String? {
        process.waitFor()
        val result = BufferedReader(InputStreamReader(process.inputStream))
            .readLines().joinToString(" ")
        if (regex.isEmpty()) {
            return result
        }
        return Regex(regex).find(result)?.destructured?.component1()
    }

    private fun deleteFile(path: String): Boolean {
        return deleteFile(File(path))
    }

    private fun deleteFile(file: File): Boolean {
        if (!file.exists()) {
            return false
        }
        for (subFile in file.listFiles() ?: emptyArray()) {
            if (subFile.isFile) {
                subFile.delete()
            } else {
                deleteFile(subFile)
            }
        }
        return file.delete()
    }

    fun cleanCache(controller: DofusTreasureBotGUIController, logItem: LogItem? = null) {
        val cacheCleanLog = controller.log("Cleaning cache ...", logItem)
        val cacheDirectory = DofusProfileManager.getDofusConfDirectory()
        deleteFile(cacheDirectory.absolutePath + "/maps")
        deleteFile(cacheDirectory.absolutePath + "/Local Store")
        deleteFile(cacheDirectory.absolutePath + "/LoadingScreen.dat")
        controller.closeLog("OK", cacheCleanLog)
    }

    private fun waitUntil(condition: () -> Boolean, timeOutMillis: Long = 30000) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOutMillis) {
            if (condition.invoke()) {
                return
            }
        }
        error("Timeout exception")
    }

    fun bringGameToFront(screen: GraphicsDevice) {
        val handle = getHandle() ?: error("No handle found for game screen")
        User32.INSTANCE.SetForegroundWindow(handle)
        val screenBounds = screen.defaultConfiguration.bounds
        User32.INSTANCE.MoveWindow(
            handle,
            screenBounds.x,
            screenBounds.y,
            screenBounds.width,
            screenBounds.height,
            false
        )
        // 3 Corresponds to the "Maximize" command
        User32.INSTANCE.ShowWindow(handle, 3)
    }

}