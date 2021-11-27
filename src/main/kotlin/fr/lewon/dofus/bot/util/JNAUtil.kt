package fr.lewon.dofus.bot.util

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object JNAUtil {

    fun getFrameName(pid: Long): String {
        val handle = findByPID(pid) ?: return ""
        val text = CharArray(1024)
        User32.INSTANCE.GetWindowText(handle, text, text.size)
        return Native.toString(text)
    }

    fun findByPID(pid: Long): HWND? {
        val windows = ArrayList<HWND>()
        User32.INSTANCE.EnumWindows({ handle, _ ->
            val pidRef = IntByReference()
            User32.INSTANCE.GetWindowThreadProcessId(handle, pidRef)
            if (pidRef.value.toLong() == pid) {
                windows.add(handle)
            }
            true
        }, null)
        if (windows.isEmpty()) {
            return null
        }
        return windows[0]
    }

    fun updateGameBounds(gameInfo: GameInfo, pid: Long) {
        try {
            gameInfo.lock.lock()
            val rect = WinDef.RECT()
            User32.INSTANCE.GetClientRect(findByPID(pid), rect)

            var x = 0
            var y = 0
            var width = rect.right - rect.left
            var height = rect.bottom - rect.top

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

            gameInfo.bounds = Rectangle(x, y, width, height)
        } finally {
            gameInfo.lock.unlock()
        }
    }

    fun openGame(): Long {
        val gameLoc = "${VldbFilesUtil.getDofusDirectory()}/Dofus.exe"
        val gameFile = File(gameLoc)
        if (!gameFile.exists() || !gameFile.isFile) {
            error("Unable to find Dofus.exe in dofus directory.")
        }
        val processBuilder = ProcessBuilder(
            "cmd",
            "/c",
            "wmic process call create \"${gameFile.absolutePath}\", \"${gameFile.parentFile.absolutePath}\" | findStr ProcessId"
        )
        val pidRegex = Regex("ProcessId = ([0-9]+);")
        val pidResults = exec(processBuilder)
            .mapNotNull { pidRegex.find(it) }
            .map { it.destructured.component1() }
        if (pidResults.isEmpty()) {
            error("Failed to launch game")
        }
        return pidResults[0].toLong()
    }

    fun takeCapture(gameInfo: GameInfo): BufferedImage {
        try {
            gameInfo.lock.lock()
            val pid = gameInfo.pid
            val handle = findByPID(pid) ?: error("Can't take capture, no handle for PID : $pid")
            val hdcWindow = User32.INSTANCE.GetDC(handle)
            val hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow)

            val bounds = WinDef.RECT()
            User32.INSTANCE.GetClientRect(handle, bounds)

            val width = bounds.right - bounds.left
            val height = bounds.bottom - bounds.top

            val hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height)

            val hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap)
            val srcCopy = 0x00CC0020
            GDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, srcCopy)

            GDI32.INSTANCE.SelectObject(hdcMemDC, hOld)
            GDI32.INSTANCE.DeleteDC(hdcMemDC)

            val bmi = WinGDI.BITMAPINFO()
            bmi.bmiHeader.biWidth = width
            bmi.bmiHeader.biHeight = -height
            bmi.bmiHeader.biPlanes = 1
            bmi.bmiHeader.biBitCount = 32
            bmi.bmiHeader.biCompression = WinGDI.BI_RGB

            val buffer = Memory(width * height * 4L)
            GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS)

            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width)

            GDI32.INSTANCE.DeleteObject(hBitmap)
            User32.INSTANCE.ReleaseDC(handle, hdcWindow)
            return image
        } finally {
            gameInfo.lock.unlock()
        }
    }

    private fun exec(processBuilder: ProcessBuilder): List<String> {
        val process = processBuilder.start()
        process.waitFor()
        val input = BufferedReader(InputStreamReader(process.inputStream))
        return input.readLines()
    }

    fun isGameEnabled(pid: Long): Boolean {
        return User32.INSTANCE.IsWindowEnabled(findByPID(pid))
    }

    fun closeGame(pid: Long) {
        User32.INSTANCE.PostMessage(findByPID(pid), WinUser.WM_CLOSE, null, null)
    }

    fun getGamePosition(pid: Long): Point {
        val rect = WinDef.RECT()
        User32.INSTANCE.GetWindowRect(findByPID(pid), rect)
        return Point(rect.left + 8, rect.top + 8 + 23)
    }

    fun getGameSize(pid: Long): Point {
        val bounds = WinDef.RECT()
        val handle = findByPID(pid) ?: error("Can't take capture, no handle for PID : $pid")
        User32.INSTANCE.GetClientRect(handle, bounds)

        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top
        return Point(width, height)
    }

}