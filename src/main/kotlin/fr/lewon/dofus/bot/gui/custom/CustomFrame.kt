package fr.lewon.dofus.bot.gui.custom

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.border.LineBorder

open class CustomFrame(
    title: String,
    w: Int,
    h: Int,
    headerColor: Color,
    val headerHeight: Int,
    iconUrl: URL? = null,
    reduceButton: Boolean = true,
    closeButton: Boolean = true
) : JFrame(title) {

    var offset: Point = Point(0, 0)

    init {
        getRootPane().border = LineBorder(headerColor, 2)
        contentPane.layout = null
        contentPane.background = headerColor
        size = Dimension(w, h)
        if (reduceButton) {
            val lblReduce = JLabel("-")
            lblReduce.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    state = ICONIFIED
                }

                override fun mouseEntered(e: MouseEvent) {
                    lblReduce.foreground = Color.BLACK
                }

                override fun mouseExited(e: MouseEvent) {
                    lblReduce.foreground = Color.WHITE
                }
            })
            lblReduce.horizontalAlignment = SwingConstants.CENTER
            lblReduce.verticalAlignment = SwingConstants.TOP
            lblReduce.foreground = Color.WHITE
            lblReduce.font = Font("Tahoma", Font.PLAIN, 20)
            lblReduce.setBounds(size.width - 2 * 30, 0, 30, 30)
            contentPane.add(lblReduce)
        }

        if (closeButton) {
            val lblClose = JLabel("x")
            lblClose.verticalAlignment = SwingConstants.BOTTOM
            lblClose.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    dispose()
                }

                override fun mouseEntered(e: MouseEvent) {
                    lblClose.foreground = Color.BLACK
                }

                override fun mouseExited(e: MouseEvent) {
                    lblClose.foreground = Color.WHITE
                }
            })
            lblClose.horizontalAlignment = SwingConstants.CENTER
            lblClose.verticalAlignment = SwingConstants.TOP
            lblClose.foreground = Color.WHITE
            lblClose.font = Font("Tahoma", Font.PLAIN, 20)
            lblClose.setBounds(size.width - 30, 0, 30, 30)
            contentPane.add(lblClose)
        }

        val lblBd = JLabel(title)
        lblBd.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                offset = mousePosition
            }
        })
        lblBd.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                val point = MouseInfo.getPointerInfo().location
                setLocation(
                    point.x - offset.getX().toInt(),
                    point.y - offset.getY().toInt()
                )
            }
        })

        lblBd.isOpaque = true
        lblBd.background = contentPane.background
        lblBd.foreground = Color.WHITE
        lblBd.font = Font("Times New Roman", Font.BOLD, 20)
        if (iconUrl != null) {
            lblBd.icon = ImageIcon(iconUrl)
        }
        lblBd.setBounds(0, 0, size.width, headerHeight)
        contentPane.add(lblBd)
    }

}