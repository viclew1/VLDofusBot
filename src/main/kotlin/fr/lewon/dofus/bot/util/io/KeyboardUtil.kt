package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.util.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent


object KeyboardUtil {

    fun sendKey(gameInfo: GameInfo, keyEvent: Int, time: Int = 100) {
        doSendKey(getHandle(gameInfo), keyEvent)
        WaitUtil.sleep(time)
    }

    fun sendSysKey(gameInfo: GameInfo, keyEvent: Int, time: Int = 100) {
        sendMessages(
            getHandle(gameInfo),
            listOf(WinUser.WM_SYSKEYDOWN, WinUser.WM_CHAR, WinUser.WM_SYSKEYUP),
            keyEvent.toLong(),
            0
        )
        WaitUtil.sleep(time)
    }

    private fun sendMessages(handle: WinDef.HWND, messageTypes: List<Int>, wParamValue: Long, lParamValue: Long) {
        for (messageType in messageTypes.withIndex()) {
            User32.INSTANCE.SendMessage(handle, messageType.value, WinDef.WPARAM((wParamValue)), LPARAM(lParamValue))
            if (messageType.index != messageTypes.lastIndex) {
                WaitUtil.sleep(10)
            }
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
        val handle = getHandle(gameInfo)
        val oldClipBoard = getClipboard()
        setClipboard(text)
        val wParamCtrl = WinDef.WPARAM(KeyEvent.VK_CONTROL.toLong())
        User32.INSTANCE.SendMessage(handle, WinUser.WM_KEYDOWN, wParamCtrl, LPARAM(0x1000000))
        doSendKey(handle, KeyEvent.VK_V)
        User32.INSTANCE.SendMessage(handle, WinUser.WM_KEYUP, wParamCtrl, LPARAM(0x1000000))
        setClipboard(oldClipBoard)
        WaitUtil.sleep(time)
    }

    private fun getHandle(gameInfo: GameInfo): WinDef.HWND {
        val pid = gameInfo.pid
        return JNAUtil.findByPID(pid) ?: error("Couldn't press key, no handle for PID : $pid")
    }

    private fun setClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
    }

    private fun getClipboard(): String {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        return clipboard.getData(DataFlavor.stringFlavor) as String
    }

}