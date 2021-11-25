package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.game.MousePositionsUtil
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.network.GameInfo

object MouseUtil {

    const val WM_LBUTTONDOWN = 0x0201
    const val WM_LBUTTONUP = 0x0202
    const val WM_MOUSEMOVE = 0x0200

    fun leftClick(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 0, moveToRestPos: Boolean = true) {
        val pid = gameInfo.pid
        val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
        doLeftClick(handle, position)
        WaitUtil.sleep(millis)
        if (moveToRestPos) {
            doMove(handle, ConverterUtil.toPointAbsolute(gameInfo, MousePositionsUtil.getRestPosition(gameInfo)))
        }
    }

    private fun doLeftClick(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        User32.INSTANCE.SendMessage(handle, WM_LBUTTONDOWN, WinDef.WPARAM(1), lParam)
        WaitUtil.sleep(40)
        User32.INSTANCE.SendMessage(handle, WM_LBUTTONUP, WinDef.WPARAM(0), lParam)
    }

    fun move(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 0) {
        val pid = gameInfo.pid
        val handle = JNAUtil.findByPID(pid) ?: error("Couldn't click, no handle for PID : $pid")
        doMove(handle, position)
        WaitUtil.sleep(millis)
    }

    private fun doMove(handle: WinDef.HWND, position: PointAbsolute) {
        val lParam = makeLParam(position.x, position.y)
        User32.INSTANCE.SendMessage(handle, WM_MOUSEMOVE, WinDef.WPARAM(1), lParam)
        WaitUtil.sleep(10)
    }

    private fun makeLParam(x: Int, y: Int): WinDef.LPARAM {
        return WinDef.LPARAM(((y shl 16) or (x and 0xFFFF)).toLong())
    }

    fun leftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 0, moveToRestPos: Boolean = true) {
        leftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis, moveToRestPos)
    }

    fun move(gameInfo: GameInfo, position: PointRelative, millis: Int = 0) {
        move(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis)
    }

    fun doubleLeftClick(gameInfo: GameInfo, position: PointAbsolute, millis: Int = 0, moveToRestPos: Boolean = true) {
        leftClick(gameInfo, position, 20, moveToRestPos)
        leftClick(gameInfo, position, millis, moveToRestPos)
    }

    fun doubleLeftClick(gameInfo: GameInfo, position: PointRelative, millis: Int = 0, moveToRestPos: Boolean = true) {
        doubleLeftClick(gameInfo, ConverterUtil.toPointAbsolute(gameInfo, position), millis, moveToRestPos)
    }

}