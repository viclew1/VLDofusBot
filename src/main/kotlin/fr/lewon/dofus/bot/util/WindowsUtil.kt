package fr.lewon.dofus.bot.util

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.util.io.WaitUtil
import java.awt.Rectangle
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object WindowsUtil {

    private const val PROCESS_NAME = "Dofus.exe"

    private fun taskPidList(): List<String> {
        val lines = ArrayList<String>()
        val tasklist: Process = Runtime.getRuntime().exec("tasklist")
        val input = BufferedReader(InputStreamReader(tasklist.inputStream))
        for (line in input.lines()) {
            val lineSplit = line.replace(Regex(" +"), " ").split(" ")
            if (lineSplit[0] == PROCESS_NAME) {
                lines.add(lineSplit[1])
            }
        }
        input.close()
        return lines
    }

    private fun findByPID(pid: Long): HWND? {
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
            return null
        }
        return windows[0]
    }

    private fun getHandle(): HWND? {
        val pidList = taskPidList()
        if (pidList.isEmpty()) {
            return null
        }
        if (pidList.size != 1) {
            error("Multiple Dofus frames opened (${pidList.size}), please only let one opened")
        }
        return findByPID(pidList[0].toLong())
    }

    fun isGameOpen(): Boolean {
        return getHandle() != null
    }

    fun updateGameBounds() {
        val rect = WinDef.RECT()
        User32.INSTANCE.GetWindowRect(getHandle(), rect)
        val windowBounds = rect.toRectangle()
        val windowBarHeight = 23
        val maxHeight = 1080 - 23

        val dx = 8
        val dyBottom = 8
        val dyTop = 8 + windowBarHeight

        var x = windowBounds.x + dx
        var width = windowBounds.width - 2 * dx
        var y = windowBounds.y + dyTop
        var height = windowBounds.height - (dyTop + dyBottom)

        val completeBounds = Rectangle(x, y, width, height)

        val targetRatio = 5f / 4f
        val ratio = width.toFloat() / height.toFloat()
        val keepWidth = ratio < targetRatio
        if (keepWidth) {
            val newHeight = (width / targetRatio).toInt()
            y += (height - newHeight) / 2
            height = newHeight
        } else {
            val newWidth = (height * targetRatio).toInt()
            x += (width - newWidth) / 2
            width = newWidth
        }

        GameInfo.completeBounds = completeBounds
        GameInfo.sizeRatio = height.toFloat() / maxHeight
        GameInfo.bounds = Rectangle(x, y, width, height)
    }

    fun openGame() {
        if (isGameOpen()) {
            error("Game already opened")
        }
        val locateGameProcessBuilder = ProcessBuilder("cmd", "/c", "where $PROCESS_NAME")
        val gameLocResults = exec(locateGameProcessBuilder)
        if (gameLocResults.isEmpty()) {
            error("Unable to find $PROCESS_NAME, is it in your path?")
        }
        val gameFile = File(gameLocResults[0])
        if (!gameFile.exists() || !gameFile.isFile) {
            error("Unable to find $PROCESS_NAME, is it in your path?")
        }
        val processBuilder = ProcessBuilder(
            "cmd",
            "/c",
            "wmic process call create \"${gameFile.absolutePath}\", \"${gameFile.parentFile.absolutePath}\" | findStr ProcessId"
        )
        val pidResults = exec(processBuilder, "ProcessId = ([0-9]+);")
        if (pidResults.isEmpty()) {
            error("Failed to launch game")
        }
        val gamePid = pidResults[0].toLong()
        if (!WaitUtil.waitUntil({ getHandle() != null })) {
            error("Couldn't retrieve handle")
        }
        if (!WaitUtil.waitUntil({ User32.INSTANCE.IsWindowEnabled(getHandle()) })) {
            error("Dofus window isn't enabled")
        }
    }

    private fun exec(processBuilder: ProcessBuilder, regexStr: String = ""): List<String> {
        val process = processBuilder.start()
        process.waitFor()
        val input = BufferedReader(InputStreamReader(process.inputStream))
        val result = input.readLines()
        if (regexStr.isEmpty()) {
            return result
        }
        val regex = Regex(regexStr)
        return result.mapNotNull { regex.find(it) }
            .map { it.destructured.component1() }
    }

    fun bringGameToFront() {
        User32.INSTANCE.SetForegroundWindow(getHandle())
    }

}