package fr.lewon.dofus.bot.util.network

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.io.KeyboardUtil
import java.awt.event.KeyEvent


object TestJNA {


    const val WM_RBUTTONDOWN = 0x0204
    const val WM_RBUTTONUP = 0x0205
    const val WM_LBUTTONDOWN = 0x0201
    const val WM_LBUTTONUP = 0x0202

    fun mouseClick(handle: WinDef.HWND, x: Int, y: Int) {
        val pt = WinDef.POINT(x, y)
        val lParam = makeLParam(x, y)
        User32.INSTANCE.PostMessage(handle, WM_LBUTTONDOWN, WinDef.WPARAM(1), lParam)
        User32.INSTANCE.PostMessage(handle, WM_LBUTTONUP, WinDef.WPARAM(0), lParam)
    }

    private fun makeLParam(x: Int, y: Int): WinDef.LPARAM {
        return WinDef.LPARAM(((y shl 16) or (x and 0xFFFF)).toLong())
    }

    fun sendKey(handle: WinDef.HWND, keyCode: Int, shift: Boolean = false) {
        User32.INSTANCE.PostMessage(handle, WinUser.WM_KEYUP, WinDef.WPARAM(keyCode.toLong()), WinDef.LPARAM(0))
    }

    fun sendKey(handle: WinDef.HWND, key: Char) {
        sendKey(handle, KeyEvent.getExtendedKeyCodeForChar(key.code))
    }

}

fun main() {
    GameSnifferUtil.updateNetwork()
    val pid = GameSnifferUtil.getAllPIDs()
        .onEach { println(it) }
        .first()
    val gameInfo = GameSnifferUtil.getGameInfoByPID(pid)
    JNAUtil.updateGameBounds(gameInfo, pid)
    KeyboardUtil.writeKeyboard(gameInfo, "Champs de Cania")
    //TestJNA.mouseClick(handle, 800, 350)
    println("ok")
}