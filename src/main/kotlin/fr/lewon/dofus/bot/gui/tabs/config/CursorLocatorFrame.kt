package fr.lewon.dofus.bot.gui.tabs.config

import fr.lewon.dofus.bot.util.geometry.PointAbsolute
import java.awt.Color
import java.awt.MouseInfo
import java.awt.event.*
import java.awt.geom.Ellipse2D
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants

class CursorLocatorFrame(text: String, onLocate: (PointAbsolute) -> Unit) : JFrame() {

    init {
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(arg0: ComponentEvent) {
                shape = Ellipse2D.Double(0.0, 0.0, width.toDouble(), height.toDouble())
            }
        })
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(arg0: MouseEvent) {
                val point = MouseInfo.getPointerInfo().location
                setLocation(point.x - width / 2, point.y - height / 2)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                val point = MouseInfo.getPointerInfo().location
                onLocate.invoke(PointAbsolute(point.x, point.y))
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                dispose()
            }
        })
        isAlwaysOnTop = true
        isUndecorated = true
        contentPane.background = Color.GRAY
        opacity = 0.60f
        setBounds(200, 200, 400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        val point = MouseInfo.getPointerInfo().location
        setLocation(point.x - width / 2, point.y - height / 2)
        val textLabel = JLabel(text)
        textLabel.setBounds(0, 0, width, height)
        textLabel.foreground = Color.WHITE
        textLabel.horizontalAlignment = SwingConstants.CENTER
        textLabel.verticalAlignment = SwingConstants.CENTER
        add(textLabel)
    }

}