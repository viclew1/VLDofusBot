package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.network.GameInfo

object MouseUtil {

    private const val WM_LBUTTONDOWN = 0x0201
    private const val WM_LBUTTONUP = 0x0202
    private const val WM_MOUSEMOVE = 0x0200

    fun leftClick(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100) {
        try {
            gameInfo.lock.lock()
            Thread {
                try {
                    gameInfo.lock.lock()
                    val pid = gameInfo.pid
                    val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
                    doLeftClick(handle, position)
                    WaitUtil.sleep(millis)
                } finally {
                    gameInfo.lock.unlock()
                }
            }.start()
        } finally {
            gameInfo.lock.unlock()
        }
    }

    private fun doLeftClick(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        val wParam = WinDef.WPARAM(0)
        doSendMouseMessage(handle, WM_MOUSEMOVE, wParam, lParam)
        doSendMouseMessage(handle, WM_LBUTTONDOWN, wParam, lParam)
        doSendMouseMessage(handle, WM_LBUTTONUP, wParam, lParam)
    }

    private fun doSendMouseMessage(handle: WinDef.HWND, message: Int, wParam: WinDef.WPARAM, lParam: WinDef.LPARAM) {
        SystemKeyLock.lock()
        User32.INSTANCE.SendMessage(handle, message, wParam, lParam)
        SystemKeyLock.unlock()
        WaitUtil.sleep(20)
    }

    fun move(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100) {
        try {
            gameInfo.lock.lock()
            Thread {
                try {
                    gameInfo.lock.lock()
                    val pid = gameInfo.pid
                    val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
                    doSendMouseMessage(handle, WM_MOUSEMOVE, WinDef.WPARAM(0), makeLParam(position.x, position.y))
                    WaitUtil.sleep(millis)
                } finally {
                    gameInfo.lock.unlock()
                }
            }.start()
        } finally {
            gameInfo.lock.unlock()
        }
    }

    private fun makeLParam(x: Int, y: Int): WinDef.LPARAM {
        return WinDef.LPARAM(((y shl 16) or (x and 0xFFFF)).toLong())
    }

    fun leftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 100) {
        leftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

    fun move(gameInfo: GameInfo, position: PointRelative, millis: Int = 100) {
        move(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

    fun doubleLeftClick(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 100) {
        leftClick(gameInfo, position, 250)
        leftClick(gameInfo, position, millis)
    }

    fun doubleLeftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 100) {
        doubleLeftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

}