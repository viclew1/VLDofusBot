package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.event.KeyEvent

object KeyboardUtil {

    private const val ENTER_KEY = 13

    fun enter(gameInfo: GameInfo) {
        sendKey(gameInfo, ENTER_KEY)
    }

    fun sendKey(gameInfo: GameInfo, keyEvent: Int, time: Int = 100, ctrlModifier: Boolean = false) {
        gameInfo.executeThreadedSyncOperation {
            val handle = getHandle(gameInfo)
            doSendKey(handle, keyEvent, ctrlModifier)
            WaitUtil.sleep(time)
        }
    }

    fun sendKey(gameInfo: GameInfo, key: Char, time: Int = 100, ctrlModifier: Boolean = false) {
        sendKey(gameInfo, key.code, time, ctrlModifier)
    }

    private fun doSendKey(handle: WinDef.HWND, keyEvent: Int, ctrlModifier: Boolean) {
        try {
            SystemKeyLock.lockInterruptibly()
            if (ctrlModifier) {
                sendUser32Message(handle, WinUser.WM_KEYDOWN, KeyEvent.VK_CONTROL)
                sendUser32Message(handle, WinUser.WM_CHAR, KeyEvent.VK_CONTROL)
            }
            sendUser32Message(handle, WinUser.WM_KEYDOWN, keyEvent)
            sendUser32Message(handle, WinUser.WM_CHAR, keyEvent)
            sendUser32Message(handle, WinUser.WM_KEYUP, keyEvent)
            if (ctrlModifier) {
                sendUser32Message(handle, WinUser.WM_KEYUP, KeyEvent.VK_CONTROL)
            }
        } finally {
            SystemKeyLock.unlock()
        }
    }

    fun writeKeyboard(gameInfo: GameInfo, text: String, time: Int = 500) {
        gameInfo.executeThreadedSyncOperation {
            val handle = getHandle(gameInfo)
            text.forEach {
                doSendKey(handle, it.code, false)
            }
            WaitUtil.sleep(time)
        }
    }

    private fun sendUser32Message(handle: WinDef.HWND, message: Int, wParam: Int) {
        User32.INSTANCE.SendMessage(handle, message, WinDef.WPARAM((wParam.toLong())), LPARAM(0))
    }

    private fun getHandle(gameInfo: GameInfo): WinDef.HWND {
        val pid = gameInfo.connection.pid
        return JNAUtil.findByPID(pid) ?: error("Couldn't press key, no handle for PID : $pid")
    }

}