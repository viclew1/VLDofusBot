package fr.lewon.dofus.bot.gui.tabs.config

import fr.lewon.dofus.bot.core.model.move.Direction
import fr.lewon.dofus.bot.game.GameInfo
import fr.lewon.dofus.bot.gui.util.ImageUtil
import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import net.miginfocom.swing.MigLayout
import java.awt.Insets
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JPanel

class AccessSelectorPane : JPanel(MigLayout()) {

    private val topAccessSelector =
        buildButton(Direction.TOP, "/icon/directions/arrow_top.png", "/icon/directions/arrow_top_fill.png")
    private val leftAccessSelector =
        buildButton(Direction.LEFT, "/icon/directions/arrow_left.png", "/icon/directions/arrow_left_fill.png")
    private val rightAccessSelector =
        buildButton(Direction.RIGHT, "/icon/directions/arrow_right.png", "/icon/directions/arrow_right_fill.png")
    private val bottomAccessSelector =
        buildButton(Direction.BOTTOM, "/icon/directions/arrow_bottom.png", "/icon/directions/arrow_bottom_fill.png")

    init {
        add(topAccessSelector, "cell 1 0")
        add(leftAccessSelector, "cell 0 1")
        add(rightAccessSelector, "cell 2 1")
        add(bottomAccessSelector, "cell 1 2")
    }

    private fun buildButton(dir: Direction, iconPath: String, iconFillPath: String): JButton {
        val imageUrl = javaClass.getResource(iconPath)
        val imageFilledUrl = javaClass.getResource(iconFillPath)
        val button = JButton()
        button.isBorderPainted = false
        button.border = null
        button.margin = Insets(0, 0, 0, 0)
        button.isContentAreaFilled = false
        button.icon = ImageIcon(ImageUtil.getScaledImage(imageUrl, 30, 30))
        button.rolloverIcon = ImageIcon(ImageUtil.getScaledImage(imageFilledUrl, 30, 30))
        button.addActionListener { registerDirection(dir) }
        return button
    }

    private fun registerDirection(dir: Direction) {
        val locatorFrame = CursorLocatorFrame("Click on position to register") {
            val currentMap = GameInfo.currentMap
            ConfigManager.editConfig { cfg -> cfg.moveAccessStore.registerAccessPoint(currentMap.id, dir, it) }
        }
        locatorFrame.isVisible = true
    }
}