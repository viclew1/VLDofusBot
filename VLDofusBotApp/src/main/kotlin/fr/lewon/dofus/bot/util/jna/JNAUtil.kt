package fr.lewon.dofus.bot.util.jna

import com.sun.jna.Native
import com.sun.jna.platform.win32.GDI32Util
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.ptr.IntByReference
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage


object JNAUtil {

    fun getFrameName(pid: Long): String {
        val handle = findByPID(pid) ?: return ""
        val text = CharArray(1024)
        User32.INSTANCE.GetWindowText(handle, text, text.size)
        return Native.toString(text)
    }

    fun findByPID(pid: Long): HWND? {
        val foundHandlesWithSizes = HashMap<HWND, Int>()
        User32.INSTANCE.EnumWindows({ handle, _ ->
            val pidRef = IntByReference()
            User32.INSTANCE.GetWindowThreadProcessId(handle, pidRef)
            if (pidRef.value.toLong() == pid) {
                val rect = WinDef.RECT()
                User32.INSTANCE.GetClientRect(handle, rect)
                val width = rect.right - rect.left
                val height = rect.bottom - rect.top
                val isHandleValid = width > 10 && height > 10 && User32.INSTANCE.IsWindowVisible(handle)
                if (isHandleValid) {
                    foundHandlesWithSizes[handle] = width * height
                }
            }
            true
        }, null)
        return foundHandlesWithSizes.entries.maxByOrNull { it.value }?.key
    }

    fun updateGameBounds(gameInfo: GameInfo) {
        gameInfo.lock.executeSyncOperation {
            val rect = WinDef.RECT()
            User32.INSTANCE.GetClientRect(findByPID(gameInfo.connection.pid), rect)

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
        }
    }

    fun takeCapture(gameInfo: GameInfo): BufferedImage {
        return gameInfo.lock.executeSyncOperation {
            val pid = gameInfo.connection.pid
            val handle = findByPID(pid) ?: error("Can't take capture, no handle for PID : $pid")
            GDI32Util.getScreenshot(handle)
        }
    }

    fun getGamePosition(pid: Long): Point {
        val rect = WinDef.RECT()
        User32.INSTANCE.GetWindowRect(findByPID(pid), rect)
        return Point(rect.left + 8, rect.top + 8 + 23)
    }

}