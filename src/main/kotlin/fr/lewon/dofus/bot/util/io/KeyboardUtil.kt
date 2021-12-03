package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo


object KeyboardUtil {

    fun sendKey(gameInfo: GameInfo, keyEvent: Int, time: Int = 100) {
        try {
            gameInfo.lock.lock()
            Thread {
                try {
                    gameInfo.lock.lock()
                    val handle = getHandle(gameInfo)
                    doSendKey(handle, keyEvent)
                    WaitUtil.sleep(time)
                } finally {
                    gameInfo.lock.unlock()
                }
            }.start()
        } finally {
            gameInfo.lock.unlock()
        }
    }

    fun sendKey(gameInfo: GameInfo, key: Char, time: Int = 100) {
        sendKey(gameInfo, key.code, time)
    }

    private fun doSendKey(handle: WinDef.HWND, keyEvent: Int) {
        SystemKeyLock.lock()
        User32.INSTANCE.SendMessage(handle, WinUser.WM_KEYDOWN, WinDef.WPARAM((keyEvent.toLong())), LPARAM(0))
        User32.INSTANCE.SendMessage(handle, WinUser.WM_CHAR, WinDef.WPARAM((keyEvent.toLong())), LPARAM(0))
        User32.INSTANCE.SendMessage(handle, WinUser.WM_KEYUP, WinDef.WPARAM((keyEvent.toLong())), LPARAM(0))
        SystemKeyLock.unlock()
    }

    fun writeKeyboard(gameInfo: GameInfo, text: String, time: Int = 500) {
        try {
            gameInfo.lock.lock()
            Thread {
                try {
                    gameInfo.lock.lock()
                    val handle = getHandle(gameInfo)
                    text.forEach { doSendKey(handle, it.code) }
                    WaitUtil.sleep(time)
                } finally {
                    gameInfo.lock.unlock()
                }
            }.start()
        } finally {
            gameInfo.lock.unlock()
        }
    }

    private fun getHandle(gameInfo: GameInfo): WinDef.HWND {
        val pid = gameInfo.pid
        return JNAUtil.findByPID(pid) ?: error("Couldn't press key, no handle for PID : $pid")
    }

}