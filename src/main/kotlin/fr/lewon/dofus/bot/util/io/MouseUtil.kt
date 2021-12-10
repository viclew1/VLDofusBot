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
        if (moveBeforeClick) {
            moveAround(gameInfo, position)
        }
        gameInfo.executeThreadedSyncOperation {
            val pid = gameInfo.pid
            val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
            User32.INSTANCE.SetFocus(handle)
            User32.INSTANCE.BringWindowToTop(handle)
            doLeftClick(handle, position)
            WaitUtil.sleep(millis)
        }
    }

    private fun moveAround(gameInfo: GameInfo, position: PointAbsolute) {
        val cornerPoints = listOf(
            PointAbsolute(position.x + 1, position.y),
            PointAbsolute(position.x, position.y + 1),
            PointAbsolute(position.x - 1, position.y),
            PointAbsolute(position.x, position.y - 1),
        )
        cornerPoints.forEach {
            move(gameInfo, it, 10)
        }
        cornerPoints.forEach {
            move(gameInfo, it, 10)
        }
        move(gameInfo, PointAbsolute(position.x, position.y), 10)
    }

    private fun doLeftClick(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        val wParam = WinDef.WPARAM(0)
        doSendMouseMessage(handle, WM_LBUTTONDOWN, wParam, lParam)
        doSendMouseMessage(handle, WM_LBUTTONUP, wParam, lParam)
    }

    private fun doSendMouseMessage(handle: WinDef.HWND, message: Int, wParam: WinDef.WPARAM, lParam: WinDef.LPARAM) {
        try {
            SystemKeyLock.lock()
            User32.INSTANCE.PostMessage(handle, message, wParam, lParam)
        } finally {
            SystemKeyLock.unlock()
        }
    }

    fun move(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100) {
        gameInfo.executeThreadedSyncOperation {
            val pid = gameInfo.pid
            val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
            doSendMouseMessage(handle, WM_MOUSEMOVE, WinDef.WPARAM(0), makeLParam(position.x, position.y))
            WaitUtil.sleep(millis)
        }
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