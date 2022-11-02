package fr.lewon.dofus.bot.util.io

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinUser
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import fr.lewon.dofus.bot.util.geometry.PointRelative
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent

object KeyboardUtil {

    private const val ENTER_KEY = 13

    fun enter(gameInfo: GameInfo) = sendKey(gameInfo, ENTER_KEY)

    fun sendKey(gameInfo: GameInfo, keyEvent: Int, sleepTime: Int = 100, ctrlModifier: Boolean = false) =
        gameInfo.lock.executeSyncOperation {
            val handle = getHandle(gameInfo)
            doSendKey(handle, keyEvent, ctrlModifier)
            WaitUtil.sleep(sleepTime)
        }

    fun sendKey(gameInfo: GameInfo, key: Char, sleepTime: Int = 100, ctrlModifier: Boolean = false) =
        sendKey(gameInfo, key.code, sleepTime, ctrlModifier)

    @Synchronized
    fun pasteText(
        gameInfo: GameInfo,
        text: String,
        sleepTime: Int = 100,
        clickLocationBeforePaste: PointRelative? = null
    ) = gameInfo.lock.executeSyncOperation {
        doPasteText(gameInfo, text, clickLocationBeforePaste)
        WaitUtil.sleep(sleepTime)
    }

    private fun doPasteText(gameInfo: GameInfo, text: String, clickLocationBeforePaste: PointRelative?) {
        val selection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val previousClipboardContent = clipboard.getContents(null)
        clipboard.setContents(selection, selection)
        clickLocationBeforePaste?.let { MouseUtil.leftClick(gameInfo, it, 0) }
        sendKey(gameInfo, 'V', 0, true)
        clipboard.setContents(previousClipboardContent, null)
    }

    private fun doSendKey(handle: WinDef.HWND, keyEvent: Int, ctrlModifier: Boolean) =
        SystemKeyLock.executeSyncOperation {
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
        }

    fun writeKeyboard(gameInfo: GameInfo, text: String, sleepTime: Int = 500) = gameInfo.lock.executeSyncOperation {
        val handle = getHandle(gameInfo)
        text.forEach {
            doSendKey(handle, it.code, false)
        }
        WaitUtil.sleep(sleepTime)
    }

    private fun sendUser32Message(handle: WinDef.HWND, message: Int, wParam: Int) =
        User32.INSTANCE.SendMessage(handle, message, WinDef.WPARAM((wParam.toLong())), LPARAM(0))

    private fun getHandle(gameInfo: GameInfo): WinDef.HWND {
        val pid = gameInfo.connection.pid
        return JNAUtil.findByPID(pid) ?: error("Couldn't press key, no handle for PID : $pid")
    }

}