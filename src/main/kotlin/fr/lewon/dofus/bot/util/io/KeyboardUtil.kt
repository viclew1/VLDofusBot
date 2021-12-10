package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo


object KeyboardUtil {

    private const val ENTER_KEY = 13

    fun enter(gameInfo: GameInfo) {
        sendKey(gameInfo, ENTER_KEY)
    }

    fun sendKey(gameInfo: GameInfo, keyEvent: Int, time: Int = 100) {
        gameInfo.executeThreadedSyncOperation {
            val handle = getHandle(gameInfo)
            doSendKey(handle, keyEvent)
            WaitUtil.sleep(time)
        }
    }

    fun sendKey(gameInfo: GameInfo, key: Char, time: Int = 100) {
        sendKey(gameInfo, key.code, time)
    }

    private fun doSendKey(handle: WinDef.HWND, keyEvent: Int) {
        try {
            SystemKeyLock.lock()
            User32.INSTANCE.SendMessage(handle, WinUser.WM_KEYDOWN, WinDef.WPARAM((keyEvent.toLong())), LPARAM(0))
            User32.INSTANCE.SendMessage(handle, WinUser.WM_CHAR, WinDef.WPARAM((keyEvent.toLong())), LPARAM(0))
            User32.INSTANCE.SendMessage(handle, WinUser.WM_KEYUP, WinDef.WPARAM((keyEvent.toLong())), LPARAM(0))
        } finally {
            SystemKeyLock.unlock()
        }
    }

    fun writeKeyboard(gameInfo: GameInfo, text: String, time: Int = 500) {
        gameInfo.executeThreadedSyncOperation {
            val handle = getHandle(gameInfo)
            text.forEach { doSendKey(handle, it.code) }
            WaitUtil.sleep(time)
        }
    }

    private fun getHandle(gameInfo: GameInfo): WinDef.HWND {
        val pid = gameInfo.pid
        return JNAUtil.findByPID(pid) ?: error("Couldn't press key, no handle for PID : $pid")
    }

}