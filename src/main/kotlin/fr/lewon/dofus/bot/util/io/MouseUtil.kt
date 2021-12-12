package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo

object MouseUtil {

    private const val WM_LBUTTONDOWN = 0x0201
    private const val WM_LBUTTONUP = 0x0202
    private const val WM_MOUSEMOVE = 0x0200

    fun leftClick(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100, moveBeforeClick: Boolean = true) {
        gameInfo.executeThreadedSyncOperation {
            val pid = gameInfo.pid
            val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
            User32.INSTANCE.BringWindowToTop(handle)
            try {
                SystemKeyLock.lock()
                if (moveBeforeClick) {
                    moveAround(handle, position)
                }
                WaitUtil.sleep(150)
                doLeftClick(handle, position)
            } finally {
                SystemKeyLock.unlock()
            }
            WaitUtil.sleep(millis)
        }
    }

    private fun moveAround(handle: WinDef.HWND, position: PointAbsolute) {
        val cornerPoints = listOf(
            PointAbsolute(position.x + 1, position.y),
            PointAbsolute(position.x, position.y + 1),
            PointAbsolute(position.x - 1, position.y),
            PointAbsolute(position.x, position.y - 1),
        )
        cornerPoints.forEach { doMove(handle, it) }
        doMove(handle, position)
    }

    private fun doLeftClick(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        val wParam = WinDef.WPARAM(0)
        doPostMouseMessages(handle, WM_LBUTTONDOWN, WM_LBUTTONUP, wParam = wParam, lParam = lParam)
    }

    private fun doPostMouseMessages(
        handle: WinDef.HWND,
        vararg messages: Int,
        wParam: WinDef.WPARAM,
        lParam: WinDef.LPARAM
    ) {
        messages.forEach { User32.INSTANCE.PostMessage(handle, it, wParam, lParam) }
    }

    fun move(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100) {
        gameInfo.executeThreadedSyncOperation {
            val pid = gameInfo.pid
            val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
            try {
                SystemKeyLock.lock()
                doMove(handle, position)
            } finally {
                SystemKeyLock.unlock()
            }
            WaitUtil.sleep(millis)
        }
    }

    private fun doMove(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        val wParam = WinDef.WPARAM(0)
        doPostMouseMessages(handle, WM_MOUSEMOVE, wParam = wParam, lParam = lParam)
    }

    private fun makeLParam(x: Int, y: Int): WinDef.LPARAM {
        return WinDef.LPARAM(((y shl 16) or (x and 0xFFFF)).toLong())
    }

    fun leftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 100, moveBeforeClick: Boolean = true) {
        leftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis, moveBeforeClick)
    }

    fun move(gameInfo: GameInfo, position: PointRelative, millis: Int = 100) {
        move(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

    fun doubleLeftClick(
        gameInfo: GameInfo,
        position: PointAbsolute,
        millis: Int = 100
    ) {
        leftClick(gameInfo, position, 100)
        leftClick(gameInfo, position, millis, moveBeforeClick = false)
    }

    fun doubleLeftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 100) {
        doubleLeftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

    fun tripleLeftClick(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100) {
        leftClick(gameInfo, position, 100)
        leftClick(gameInfo, position, 100, moveBeforeClick = false)
        leftClick(gameInfo, position, millis, moveBeforeClick = false)
    }

    fun tripleLeftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 100) {
        tripleLeftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

}