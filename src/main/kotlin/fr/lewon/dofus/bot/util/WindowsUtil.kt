package fr.lewon.dofus.bot.util

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import java.awt.GraphicsDevice
import java.io.BufferedReader
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

    fun bringGameToFront(screen: GraphicsDevice) {
        val pidList = taskPidList()
        if (pidList.isEmpty()) {
            error("No Dofus frame opened, please launch the game.")
        }
        if (pidList.size != 1) {
            error("Multiple Dofus frames opened (${pidList.size}), please only let one opened")
        }
        val handle = findByPID(pidList[0].toLong())
        User32.INSTANCE.SetForegroundWindow(handle)
        // 1 Corresponds to the "Show normal" command
        User32.INSTANCE.ShowWindow(handle, 1)
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