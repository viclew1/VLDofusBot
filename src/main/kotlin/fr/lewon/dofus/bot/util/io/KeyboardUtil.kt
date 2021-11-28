package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.event.KeyEvent


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

    fun sendSysKey(gameInfo: GameInfo, keyEvent: Int, time: Int = 100) {
        try {
            gameInfo.lock.lock()
            Thread {
                try {
                    gameInfo.lock.lock()
                    val handle = getHandle(gameInfo)
                    sendMessages(
                        handle,
                        listOf(WinUser.WM_SYSKEYDOWN, WinUser.WM_CHAR, WinUser.WM_SYSKEYUP),
                        keyEvent.toLong(),
                        0
                    )
                    WaitUtil.sleep(time)
                } finally {
                    gameInfo.lock.unlock()
                }
            }.start()
        } finally {
            gameInfo.lock.unlock()
        }
    }

    private fun sendMessages(handle: WinDef.HWND, messageTypes: List<Int>, wParamValue: Long, lParamValue: Long) {
        for (messageType in messageTypes) {
            SystemKeyLock.lock()
            User32.INSTANCE.SendMessage(handle, messageType, WinDef.WPARAM((wParamValue)), LPARAM(lParamValue))
            SystemKeyLock.unlock()
            WaitUtil.sleep(20)
        }
    }

    private fun doSendKey(handle: WinDef.HWND, keyEvent: Int) {
        sendMessages(
            handle,
            listOf(WinUser.WM_KEYDOWN, WinUser.WM_CHAR, WinUser.WM_KEYUP),
            keyEvent.toLong(),
            0
        )
    }

    fun writeKeyboard(gameInfo: GameInfo, text: String, time: Int = 500) {
        try {
            gameInfo.lock.lock()
            Thread {
                try {
                    gameInfo.lock.lock()
                    val handle = getHandle(gameInfo)
                    val oldClipBoard = getClipboard()
                    setClipboard(StringSelection(text))
                    SystemKeyLock.lock()
                    sendMessages(
                        handle, listOf(WinUser.WM_KEYDOWN, WinUser.WM_CHAR), KeyEvent.VK_CONTROL.toLong(), 0x1000000
                    )
                    doSendKey(handle, KeyEvent.VK_V)
                    sendMessages(handle, listOf(WinUser.WM_KEYUP), KeyEvent.VK_CONTROL.toLong(), 0x1000000)
                    setClipboard(oldClipBoard)
                    WaitUtil.sleep(time)
                } finally {
                    gameInfo.lock.unlock()
                }
            }.start()
        } finally {
            gameInfo.lock.unlock()
        }
    }

    private fun setClipboard(content: Transferable?) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(content, null)
    }

    private fun getClipboard(): Transferable? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        return clipboard.getContents(null)
    }

    private fun getHandle(gameInfo: GameInfo): WinDef.HWND {
        val pid = gameInfo.pid
        return JNAUtil.findByPID(pid) ?: error("Couldn't press key, no handle for PID : $pid")
    }

}