package fr.lewon.dofus.bot.util.io

import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import fr.lewon.dofus.bot.util.geometry.PointRelative
import java.awt.Robot
import java.awt.event.KeyEvent

object MouseUtil {

    /**
     * Places the cursor at a given location.
     * @param position - Location of the cursor in simple coordinates.
     * @param millis - Time to wait after the click in ms.
     */
    fun place(position: PointAbsolute, millis: Int = 0) {
        val robot = Robot()
        robot.mouseMove(position.x, position.y)
        WaitUtil.sleep(millis)
    }

    /**
     * Places the cursor at a given location.
     * @param position - Location of the cursor in relative coordinates.
     * @param millis - Time to wait after the click in ms.
     */
    fun place(position: PointRelative, millis: Int = 0) {
        place(ConverterUtil.toPointAbsolute(position), millis)
    }

    /**
     * Performs a left click.
     * @param position - Location of the mouse on the screen in simple coordinates.
     * @param shift - `true` if Shift must be pressed at the same time. It can be used to stack actions.
     * @param millis - Time to wait after the click in ms.
     */
    fun leftClick(position: PointAbsolute, shift: Boolean = false, millis: Int = 1000) {
        val robot = Robot()
        robot.mouseMove(position.x, position.y)
        WaitUtil.sleep(40)
        if (shift) robot.keyPress(KeyEvent.VK_SHIFT)
        robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK)
        if (shift) robot.keyRelease(KeyEvent.VK_SHIFT)
        WaitUtil.sleep(millis)
        place(ConfigManager.config.mouseRestPos)
    }

    /**
     * Performs a left click.
     * @param position - Location of the mouse on the screen in relative coordinates.
     * @param shift - `true` if Shift must be pressed at the same time. It can be used to stack actions.
     * @param millis - Time to wait after the click in ms.
     */
    fun leftClick(position: PointRelative, shift: Boolean = false, millis: Int = 1000) {
        leftClick(ConverterUtil.toPointAbsolute(position), shift, millis)
    }

    /**
     * Performs a double left click.
     * @param position - Location of the mouse on the screen in simple coordinates.
     * @param shift - `true` if Shift must be pressed at the same time. It can be used to stack actions.
     * @param millis - Time to wait after the click in ms.
     */
    fun doubleLeftClick(position: PointAbsolute, shift: Boolean = false, millis: Int = 1000) {
        leftClick(position, shift, 0)
        leftClick(position, shift, millis)
    }

    /**
     * Performs a double left click.
     * @param position - Location of the mouse on the screen in relative coordinates.
     * @param shift - `true` if Shift must be pressed at the same time. It can be used to stack actions.
     * @param millis - Time to wait after the click in ms.
     */
    fun doubleLeftClick(position: PointRelative, shift: Boolean = false, millis: Int = 1000) {
        doubleLeftClick(ConverterUtil.toPointAbsolute(position), shift, millis)
    }

    fun scrollDown(position: PointAbsolute, scrollAmount: Int = 1, timeBetweenScrolls: Int) {
        val robot = Robot()
        place(position, timeBetweenScrolls)
        for (i in 0 until scrollAmount) {
            robot.mouseWheel(1)
            place(position, timeBetweenScrolls)
        }
        place(ConfigManager.config.mouseRestPos)
    }

    fun scrollDown(position: PointRelative, scrollAmount: Int = 1, timeBetweenScrolls: Int = 300) {
        scrollDown(ConverterUtil.toPointAbsolute(position), scrollAmount, timeBetweenScrolls)
    }

}