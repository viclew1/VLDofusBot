package fr.lewon.dofus.bot.util

import java.awt.Rectangle
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage


object RobotUtil {

    private val robot = Robot()

    private val strictEquivalents = mapOf(
        Pair('&', '1'),
        Pair('é', '2'),
        Pair('"', '3'),
        Pair('\'', '4'),
        Pair('(', '5'),
        Pair('-', '6'),
        Pair('è', '7'),
        Pair('_', '8'),
        Pair('ç', '9'),
        Pair('à', '0')
    )

    private val keysShiftEquivalents = mapOf(
        Pair('/', ':'),
        Pair('1', '1'),
        Pair('2', '2'),
        Pair('3', '3'),
        Pair('4', '4'),
        Pair('5', '5'),
        Pair('6', '6'),
        Pair('7', '7'),
        Pair('8', '8'),
        Pair('9', '9'),
        Pair('0', '0')
    )

    fun screenShot(x: Int, y: Int, w: Int, h: Int): BufferedImage {
        return robot.createScreenCapture(Rectangle(x, y, w, h))
    }

    fun click(x: Int, y: Int) {
        robot.mouseMove(x, y)
        Thread.sleep(50)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(50)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(50)
    }

    fun move(x: Int, y: Int) {
        robot.mouseMove(x, y)
    }

    fun scroll(amount: Int) {
        robot.mouseWheel(amount)
    }

    fun press(keyCode: Int) {
        robot.keyPress(keyCode)
        robot.delay(20)
        robot.keyRelease(keyCode)
        robot.delay(20)
    }

    fun press(c: Char) {
        when {
            keysShiftEquivalents.containsKey(c) -> {
                val char = keysShiftEquivalents[c] ?: error("Missing shift equivalence key mapping")
                val keyCode: Int = KeyEvent.getExtendedKeyCodeForChar(char.toInt())
                robot.keyPress(KeyEvent.VK_SHIFT)
                robot.delay(10)
                robot.keyPress(keyCode)
                robot.delay(20)
                robot.keyRelease(keyCode)
                robot.delay(10)
                robot.keyRelease(KeyEvent.VK_SHIFT)
                robot.delay(20)
            }
            strictEquivalents.containsKey(c) -> {
                val char = strictEquivalents[c]?.toInt() ?: error("Missing equivalence key mapping")
                val keyCode: Int = KeyEvent.getExtendedKeyCodeForChar(char)
                robot.keyPress(keyCode)
                robot.delay(20)
                robot.keyRelease(keyCode)
                robot.delay(20)
            }
            else -> {
                val keyCode: Int = KeyEvent.getExtendedKeyCodeForChar(c.toInt())
                robot.keyPress(keyCode)
                robot.delay(20)
                robot.keyRelease(keyCode)
                robot.delay(20)
            }
        }
    }

    fun write(message: String) {
        for (c in message.toCharArray()) {
            press(c)
        }
    }

    fun enter() {
        robot.keyPress(KeyEvent.VK_ENTER)
        robot.delay(20)
        robot.keyRelease(KeyEvent.VK_ENTER)
        robot.delay(20)
    }

    fun holdShift() {
        robot.keyPress(KeyEvent.VK_SHIFT)
    }

    fun releaseShift() {
        robot.keyRelease(KeyEvent.VK_SHIFT)
    }

    fun escape() {
        robot.keyPress(KeyEvent.VK_ESCAPE)
        robot.delay(20)
        robot.keyRelease(KeyEvent.VK_ESCAPE)
        robot.delay(20)
    }

}