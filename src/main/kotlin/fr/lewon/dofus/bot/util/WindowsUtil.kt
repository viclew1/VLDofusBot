package fr.lewon.dofus.bot.util

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference
import fr.lewon.dofus.bot.ui.DofusTreasureBotGUIController
import fr.lewon.dofus.bot.ui.LogItem
import java.awt.GraphicsDevice
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object WindowsUtil {

    private val IS_WINDOWS = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0
    private const val PROCESS_NAME = "Dofus.exe"

    private fun taskPidList(): List<String> {
        val lines = ArrayList<String>()
        if (IS_WINDOWS) {
            val tasklist: Process = Runtime.getRuntime().exec("tasklist")
            val input = BufferedReader(InputStreamReader(tasklist.inputStream))
            for (line in input.lines()) {
                val lineSplit = line.replace(Regex(" +"), " ").split(" ")
                if (lineSplit[0] == PROCESS_NAME) {
                    lines.add(lineSplit[1])
                }
            }
            input.close()
        } else {
            val pidof: Process = Runtime.getRuntime().exec("ps aux | grep -i $PROCESS_NAME")
            val input = BufferedReader(InputStreamReader(pidof.inputStream))
            for (line in input.lines()) {
                val lineSplit = line.replace(Regex(" +"), " ").split(" ")
                lines.add(lineSplit[3])
            }
            input.close()
        }
        return lines
    }

    fun isGameOpen(): Boolean {
        val pidList = taskPidList()
        if (pidList.isEmpty()) {
            return false
        }
        return try {
            val handle = findByPID(pidList[0].toLong())
            handle.pointer != null
        } catch (e: Exception) {
            false
        }
    }

    fun openGame(controller: DofusTreasureBotGUIController, logItem: LogItem? = null) {
        val openingLog = controller.log("Opening game ...", logItem)
        val execFilePath =
            System.getProperty("user.home") + "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Ankama/Dofus.lnk"
        val execFile = File(execFilePath)
        if (!execFile.exists() || !execFile.isFile) {
            error("Dofus shortcut not found in directory [${execFile.parentFile.absolutePath}]")
        }
        controller.log("Dofus shortcut found, executing it ...", openingLog)
        val processBuilder = ProcessBuilder("cmd", "/c", execFile.absolutePath)
        processBuilder.start()
        controller.closeLog("Game opened", openingLog)
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

    /**
     * Closes the game and cleans the game cache when it's done
     */
    fun closeGame(controller: DofusTreasureBotGUIController, logItem: LogItem? = null) {
        val closingLog = controller.log("Closing game ...", logItem)
        User32.INSTANCE.SendMessage(getHandle(), WinUser.WM_CLOSE, null, null)

        val cacheCleanLog = controller.log("Cleaning cache ...", closingLog)
        val cacheDirectory = DofusProfileManager.getDofusConfDirectory()
        deleteFile(cacheDirectory.absolutePath + "/maps")
        deleteFile(cacheDirectory.absolutePath + "/Local Store")
        deleteFile(cacheDirectory.absolutePath + "/LoadingScreen.dat")
        controller.closeLog("OK", cacheCleanLog)

        controller.closeLog("OK", closingLog)
    }

    fun bringGameToFront(screen: GraphicsDevice) {
        val handle = getHandle()
        User32.INSTANCE.SetForegroundWindow(handle)
        // 1 Corresponds to the "Show normal" command
        User32.INSTANCE.ShowWindow(handle, 1)
        Thread.sleep(500)
        val screenBounds = screen.defaultConfiguration.bounds
        User32.INSTANCE.MoveWindow(
            handle,
            screenBounds.x,
            screenBounds.y,
            screenBounds.width,
            screenBounds.height,
            false
        )
        Thread.sleep(500)
        // 3 Corresponds to the "Maximize" command
        User32.INSTANCE.ShowWindow(handle, 3)
        Thread.sleep(500)
    }

    private fun getHandle(): HWND {
        val pidList = taskPidList()
        if (pidList.isEmpty()) {
            error("No Dofus frame opened, please launch the game.")
        }
        if (pidList.size != 1) {
            error("Multiple Dofus frames opened (${pidList.size}), please only let one opened")
        }
        return findByPID(pidList[0].toLong())
    }

    private fun findByPID(pid: Long): HWND {
        val windows = ArrayList<HWND>()
        User32.INSTANCE.EnumWindows({ hwnd, _ ->
            val pidRef = IntByReference()
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef)
            if (pidRef.value.toLong() == pid) {
                windows.add(hwnd)
            }
            true
        }, null)
        if (windows.isEmpty()) {
            error("Window for PID [$pid] not found")
        }
        return windows[0]
    }

}