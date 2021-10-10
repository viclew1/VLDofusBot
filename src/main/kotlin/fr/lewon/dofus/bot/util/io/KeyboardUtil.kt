package fr.lewon.dofus.bot.util.io

import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent

object KeyboardUtil {

    /**
     * Simulates a key press on the keyboard.
     * @param keyEvent - Integer representing the key to press.
     * @param time - Time to wait after the key press.
     */
    fun sendKey(keyEvent: Int, time: Int = 100) {
        val robot = Robot()
        robot.keyPress(keyEvent)
        robot.keyRelease(keyEvent)
        WaitUtil.sleep(time)
    }

    /**
     * Simulates key press on the keyboard.<br></br><br></br>
     * This method actually copy the string in the clipboard and paste it where the cursor is.
     * It is faster than pressing all the letters one by one.
     * @param text - Text to write.
     * @param time - Time to wait after pasting.
     */
    fun writeKeyboard(text: String, time: Int = 500) {
        val robot = Robot()
        setClipboard(text)
        WaitUtil.sleep(100)
        robot.keyPress(KeyEvent.VK_CONTROL)
        robot.keyPress(KeyEvent.VK_V)
        robot.keyRelease(KeyEvent.VK_V)
        robot.keyRelease(KeyEvent.VK_CONTROL)
        WaitUtil.sleep(time)
    }

    /**
     * Defines the text to copy in the clipboard.
     * @param text - Text to copy.
     */
    fun setClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
    }

    /**
     * Returns the current string in the clipboard.
     * @return String contained in the clipboard, `null` if the data in clipboard is not a string or if or if the clipboard is empty
     */
    fun getClipboard(): String {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        return clipboard.getData(DataFlavor.stringFlavor) as String
    }

    fun escape() {
        sendKey(KeyEvent.VK_ESCAPE)
    }

    fun enter() {
        sendKey(KeyEvent.VK_ENTER)
    }

}