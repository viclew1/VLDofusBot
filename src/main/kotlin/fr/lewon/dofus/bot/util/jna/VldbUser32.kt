package fr.lewon.dofus.bot.util.jna

import com.sun.jna.Native
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.win32.W32APIOptions


interface VldbUser32 : User32 {

    fun EnableWindow(hWnd: HWND?, bEnable: Boolean): Boolean

    companion object {
        val INSTANCE = Native.load("user32", VldbUser32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }
}