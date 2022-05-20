package fr.lewon.dofus.bot.gui.overlay

import fr.lewon.dofus.bot.game.DofusCell
import fr.lewon.dofus.bot.util.jna.JNAUtil
import fr.lewon.dofus.bot.util.network.GameInfo
import java.awt.Color
import java.awt.Dimension
import java.awt.Polygon
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.JPanel

abstract class AbstractOverlay : JFrame() {

    var hitBoxByCell: Map<DofusCell, Polygon> = HashMap()
    lateinit var gameInfo: GameInfo
    val overlayBounds by lazy {
        buildOverlayBounds()
    }

    init {
        isAlwaysOnTop = true
        isUndecorated = true
        this.contentPane = buildContentPane()
        this.contentPane.background = Color.GRAY
        opacity = 0.60f
    }

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