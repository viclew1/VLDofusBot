package fr.lewon.dofus.bot.util.jna

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
        var foundHandle: HWND? = null
        User32.INSTANCE.EnumWindows({ handle, _ ->
            val pidRef = IntByReference()
            User32.INSTANCE.GetWindowThreadProcessId(handle, pidRef)
            if (pidRef.value.toLong() == pid) {
                foundHandle = handle
                false
            } else {
                true
            }
        }, null)
        return foundHandle
    }

    fun updateGameBounds(gameInfo: GameInfo) {
        try {
            gameInfo.lock.lock()
            val rect = WinDef.RECT()
            User32.INSTANCE.GetClientRect(findByPID(gameInfo.pid), rect)

            var x = 0
            var y = 0
            var width = rect.right - rect.left
            var height = rect.bottom - rect.top
            gameInfo.completeBounds = Rectangle(x, y, width, height)

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

            gameInfo.gameBounds = Rectangle(x, y, width, height)
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
        val pid = gameInfo.pid
        val handle = findByPID(pid) ?: error("Can't take capture, no handle for PID : $pid")
        val hdcWindow = User32.INSTANCE.GetDC(handle)
        val hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow)
        try {
            gameInfo.lock.lock()

            val width = gameInfo.completeBounds.width
            val height = gameInfo.completeBounds.height
            if (width == 0 || height == 0) {
                error("Failed to take capture.")
            }

            val hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height)

            val hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap)
            val srcCopy = 0x00CC0020
            GDI32.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, srcCopy)

            GDI32.INSTANCE.SelectObject(hdcMemDC, hOld)

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
            return image
        } finally {
            GDI32.INSTANCE.DeleteDC(hdcMemDC)
            User32.INSTANCE.ReleaseDC(handle, hdcWindow)
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

}