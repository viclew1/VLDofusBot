package fr.lewon.dofus.bot.overlay

import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.info.GameInfo
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

abstract class AbstractOverlay : JFrame() {

    lateinit var gameInfo: GameInfo
    val overlayBounds by lazy {
        buildOverlayBounds()
    }

    init {
        SwingUtilities.invokeLater {
            isAlwaysOnTop = true
            isUndecorated = true
            opacity = 0.60f
            additionalInit()
            this.contentPane = buildContentPane()
            this.contentPane.background = Color.GRAY
        }
    }

    abstract fun additionalInit()

    protected abstract fun buildContentPane(): JPanel

    open fun updateOverlay(gameInfo: GameInfo) {
        this.gameInfo = gameInfo
        JNAUtil.updateGameBounds(gameInfo)
        val windowPos = JNAUtil.getGamePosition(gameInfo.connection.pid)
        val overlayBounds = buildOverlayBounds()
        bounds = Rectangle(
            overlayBounds.x + windowPos.x,
            overlayBounds.y + windowPos.y,
            overlayBounds.width,
            overlayBounds.height
        )
        contentPane.size = Dimension(gameInfo.gameBounds.width, gameInfo.gameBounds.height)
    }

    protected abstract fun buildOverlayBounds(): Rectangle

}