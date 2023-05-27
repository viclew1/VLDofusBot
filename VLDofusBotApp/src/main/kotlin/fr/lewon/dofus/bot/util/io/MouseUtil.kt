package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo

object MouseUtil {

    private const val WM_LBUTTONDOWN = 0x0201
    private const val WM_LBUTTONUP = 0x0202
    private const val WM_MOUSEMOVE = 0x0200

    fun leftClick(gameInfo: GameInfo, position: PointAbsolute, sleepTime: Int = 100, moveBeforeClick: Boolean = true) {
        gameInfo.lock.executeSyncOperation {
            val pid = gameInfo.connection.pid
            val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
            SystemKeyLock.executeSyncOperation {
                if (moveBeforeClick) {
                    moveAround(handle, position)
                    WaitUtil.sleep(120)
                }
                doLeftClick(handle, position)
            }
            WaitUtil.sleep(sleepTime)
        }
    }

    private fun moveAround(handle: WinDef.HWND, position: PointAbsolute) {
        val cornerPoints = listOf(
            PointAbsolute(position.x + 1, position.y),
            PointAbsolute(position.x, position.y - 1),
        )
        cornerPoints.forEach { doMove(handle, it) }
        doMove(handle, position)
    }

    private fun doLeftClick(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        val wParam = makeWParam(0, 0)
        User32.INSTANCE.SendMessage(handle, WM_LBUTTONDOWN, wParam, lParam)
        WaitUtil.sleep((5..15).random())
        User32.INSTANCE.SendMessage(handle, WM_LBUTTONUP, wParam, lParam)
    }

    fun move(gameInfo: GameInfo, position: PointAbsolute, sleepTime: Int = 100) {
        gameInfo.lock.executeSyncOperation {
            val pid = gameInfo.connection.pid
            val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
            SystemKeyLock.executeSyncOperation {
                doMove(handle, position)
            }
            WaitUtil.sleep(sleepTime)
        }
    }

    private fun doMove(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        val wParam = makeWParam(0, 0)
        User32.INSTANCE.SendMessage(handle, WM_MOUSEMOVE, wParam, lParam)
    }

    private fun makeLParam(low: Int, high: Int): WinDef.LPARAM {
        return WinDef.LPARAM(makeParamValue(low, high))
    }

    private fun makeWParam(low: Int, high: Int): WinDef.WPARAM {
        return WinDef.WPARAM(makeParamValue(low, high))
    }

    private fun makeParamValue(low: Int, high: Int): Long {
        return ((high shl 16) or (low and 0xFFFF)).toLong()
    }

    fun leftClick(gameInfo: GameInfo, position: PointRelative, sleepTime: Int = 100, moveBeforeClick: Boolean = true) {
        leftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), sleepTime, moveBeforeClick)
    }

    fun move(gameInfo: GameInfo, position: PointRelative, sleepTime: Int = 100) {
        move(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), sleepTime)
    }

    private fun doubleLeftClick(
        gameInfo: GameInfo,
        position: PointAbsolute,
        sleepTime: Int = 100
    ) {
        leftClick(gameInfo, position, 100)
        leftClick(gameInfo, position, sleepTime, moveBeforeClick = false)
    }

    fun doubleLeftClick(gameInfo: GameInfo, position: PointRelative, sleepTime: Int = 100) {
        doubleLeftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), sleepTime)
    }

    private fun tripleLeftClick(gameInfo: GameInfo, position: PointAbsolute, sleepTime: Int = 100) {
        leftClick(gameInfo, position, 100)
        leftClick(gameInfo, position, 100, moveBeforeClick = false)
        leftClick(gameInfo, position, sleepTime, moveBeforeClick = false)
    }

    fun tripleLeftClick(gameInfo: GameInfo, position: PointRelative, sleepTime: Int = 100) {
        tripleLeftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), sleepTime)
    }

}
